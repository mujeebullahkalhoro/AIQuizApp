package com.example.quizapp.data.repository

import com.example.quizapp.data.local.dao.QuizHistoryDao
import com.example.quizapp.data.local.entities.QuizHistoryEntity
import com.example.quizapp.data.remote.api.OpenAiApiService
import com.example.quizapp.data.remote.dto.ChatMessage
import com.example.quizapp.data.remote.dto.ChatRequest
import com.example.quizapp.domain.model.Question
import com.example.quizapp.domain.model.QuizHistory
import com.example.quizapp.domain.model.QuizResult
import com.example.quizapp.domain.repository.QuizRepository
import com.example.quizapp.utils.Constants
import com.example.quizapp.utils.NetworkChecker
import com.example.quizapp.utils.extractJsonFromResponse
import com.example.quizapp.utils.getCurrentDateString
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class QuizRepositoryImpl @Inject constructor(
    private val apiService: OpenAiApiService,
    private val quizHistoryDao: QuizHistoryDao,
    private val gson: Gson,
    private val networkChecker: NetworkChecker
) : QuizRepository {

    override suspend fun generateQuiz(topic: String, questionCount: Int): Result<List<Question>> {
        if (!networkChecker.isConnected()) {
            return Result.failure(Exception("No internet connection. Please check your network and try again."))
        }
        return try {
            val prompt = buildGeneratePrompt(topic, questionCount)
            val request = ChatRequest(
                model = Constants.OPENAI_MODEL,
                messages = listOf(
                    ChatMessage("system", "You are an expert quiz generator. Always return valid JSON only, no markdown, no explanations."),
                    ChatMessage("user", prompt)
                )
            )
            val response = apiService.createChatCompletion(
                authorization = "Bearer ${Constants.OPENAI_API_KEY}",
                request = request
            )
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string() ?: "Unknown API error"
                return Result.failure(Exception("API Error ${response.code()}: $errorBody"))
            }
            val content = response.body()?.choices?.firstOrNull()?.message?.content
                ?: return Result.failure(Exception("Empty response from API"))

            val cleanJson = extractJsonFromResponse(content)
            val jsonObject = JsonParser.parseString(cleanJson).asJsonObject
            val questionsArray = jsonObject.getAsJsonArray("questions")
            val questions = mutableListOf<Question>()
            for (element in questionsArray) {
                val obj = element.asJsonObject
                val optionsType = object : TypeToken<List<String>>() {}.type
                val question = Question(
                    question = obj.get("question").asString,
                    options = gson.fromJson(obj.getAsJsonArray("options"), optionsType),
                    correctAnswer = obj.get("correctAnswer").asString
                )
                questions.add(question)
            }
            Result.success(questions)
        } catch (e: UnknownHostException) {
            Result.failure(Exception("Cannot reach server. Please check your internet connection."))
        } catch (e: SocketTimeoutException) {
            Result.failure(Exception("Request timed out. The server is taking too long to respond."))
        } catch (e: IOException) {
            Result.failure(Exception("Network error. Please check your connection and try again."))
        } catch (e: Exception) {
            Result.failure(Exception("Failed to generate quiz: ${e.message}"))
        }
    }

    override suspend fun evaluateQuiz(
        questions: List<Question>,
        selectedAnswers: Map<Int, String>
    ): Result<QuizResult> {
        return try {
            var correctCount = 0
            questions.forEachIndexed { index, question ->
                val selected = selectedAnswers[index] ?: ""
                if (selected.trim().equals(question.correctAnswer.trim(), ignoreCase = true)) {
                    correctCount++
                }
            }
            val wrongCount = questions.size - correctCount
            val score = if (questions.isNotEmpty()) (correctCount.toFloat() / questions.size) * 100f else 0f

            val feedback = try {
                if (networkChecker.isConnected()) fetchFeedbackFromApi(questions, selectedAnswers, score)
                else generateLocalFeedback(score)
            } catch (e: UnknownHostException) {
                generateLocalFeedback(score)
            } catch (e: SocketTimeoutException) {
                generateLocalFeedback(score)
            } catch (e: Exception) {
                generateLocalFeedback(score)
            }

            Result.success(
                QuizResult(
                    correctCount = correctCount,
                    wrongCount = wrongCount,
                    score = score,
                    feedback = feedback,
                    totalQuestions = questions.size
                )
            )
        } catch (e: Exception) {
            Result.failure(Exception("Failed to evaluate quiz: ${e.message}"))
        }
    }

    private suspend fun fetchFeedbackFromApi(
        questions: List<Question>,
        selectedAnswers: Map<Int, String>,
        score: Float
    ): String {
        val prompt = buildEvaluationPrompt(questions, selectedAnswers)
        val request = ChatRequest(
            model = Constants.OPENAI_MODEL,
            messages = listOf(
                ChatMessage("system", "You are a quiz evaluator. Return only valid JSON, no markdown."),
                ChatMessage("user", prompt)
            ),
            maxTokens = 500
        )
        val response = apiService.createChatCompletion(
            authorization = "Bearer ${Constants.OPENAI_API_KEY}",
            request = request
        )
        if (!response.isSuccessful) return generateLocalFeedback(score)
        val content = response.body()?.choices?.firstOrNull()?.message?.content ?: return generateLocalFeedback(score)
        val cleanJson = extractJsonFromResponse(content)
        val jsonObject = JsonParser.parseString(cleanJson).asJsonObject
        return jsonObject.get("feedback")?.asString ?: generateLocalFeedback(score)
    }

    override suspend fun saveQuizHistory(
        userId: Int,
        topic: String,
        questions: List<Question>,
        selectedAnswers: Map<Int, String>,
        result: QuizResult
    ): Result<Unit> {
        return try {
            val entity = QuizHistoryEntity(
                userId = userId,
                topic = topic,
                questionCount = questions.size,
                correctAnswers = result.correctCount,
                wrongAnswers = result.wrongCount,
                score = result.score,
                feedback = result.feedback,
                date = getCurrentDateString(),
                questionsJson = gson.toJson(questions),
                selectedAnswersJson = gson.toJson(selectedAnswers)
            )
            quizHistoryDao.insertQuizHistory(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to save quiz history: ${e.message}"))
        }
    }

    override fun getQuizHistoryByUser(userId: Int): Flow<List<QuizHistory>> {
        return quizHistoryDao.getQuizHistoryByUser(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getQuizHistoryById(quizId: Int): QuizHistory? {
        return quizHistoryDao.getQuizHistoryById(quizId)?.toDomain()
    }

    override suspend fun deleteQuizHistory(quizId: Int): Result<Unit> {
        return try {
            quizHistoryDao.deleteQuizHistoryById(quizId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to delete quiz: ${e.message}"))
        }
    }

    override suspend fun getQuizCount(userId: Int): Int {
        return quizHistoryDao.getQuizCountByUser(userId)
    }

    override suspend fun getAverageScore(userId: Int): Float {
        return quizHistoryDao.getAverageScoreByUser(userId) ?: 0f
    }

    private fun QuizHistoryEntity.toDomain(): QuizHistory {
        val questionsType = object : TypeToken<List<Question>>() {}.type
        val answersType = object : TypeToken<Map<Int, String>>() {}.type
        val questionsList: List<Question> = try {
            gson.fromJson(questionsJson, questionsType) ?: emptyList()
        } catch (e: Exception) { emptyList() }
        val answersMap: Map<Int, String> = try {
            gson.fromJson(selectedAnswersJson, answersType) ?: emptyMap()
        } catch (e: Exception) { emptyMap() }
        return QuizHistory(
            id = id,
            userId = userId,
            topic = topic,
            questionCount = questionCount,
            correctAnswers = correctAnswers,
            wrongAnswers = wrongAnswers,
            score = score,
            feedback = feedback,
            date = date,
            questions = questionsList,
            selectedAnswers = answersMap
        )
    }

    private fun buildGeneratePrompt(topic: String, questionCount: Int): String {
        return """Generate a quiz on topic: "$topic"
Number of questions: $questionCount

Return ONLY valid JSON in this exact format, no markdown, no extra text:
{"questions":[{"question":"...","options":["...","...","...","..."],"correctAnswer":"..."}]}

Rules:
- Exactly $questionCount questions
- MCQs only with exactly 4 options each
- correctAnswer must exactly match one of the options
- No markdown, no explanations, JSON only"""
    }

    private fun buildEvaluationPrompt(
        questions: List<Question>,
        selectedAnswers: Map<Int, String>
    ): String {
        val sb = StringBuilder()
        sb.appendLine("Evaluate these quiz answers and return ONLY valid JSON:")
        sb.appendLine("""{"feedback":"brief encouraging feedback based on performance"}""")
        sb.appendLine()
        sb.appendLine("Questions and answers:")
        questions.forEachIndexed { index, question ->
            sb.appendLine("Q${index + 1}: ${question.question}")
            sb.appendLine("Correct: ${question.correctAnswer}")
            sb.appendLine("Selected: ${selectedAnswers[index] ?: "Not answered"}")
        }
        return sb.toString()
    }

    private fun generateLocalFeedback(score: Float): String {
        return when {
            score >= 90 -> "Excellent work! You have a strong mastery of this topic."
            score >= 75 -> "Great job! You have a good understanding of the material."
            score >= 60 -> "Good effort! Review the missed questions to strengthen your knowledge."
            score >= 40 -> "Keep practicing! Focus on the areas where you made mistakes."
            else -> "Don't give up! Review the material and try again to improve your score."
        }
    }
}
