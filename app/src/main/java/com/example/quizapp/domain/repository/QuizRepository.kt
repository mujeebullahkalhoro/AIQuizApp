package com.example.quizapp.domain.repository

import com.example.quizapp.domain.model.Question
import com.example.quizapp.domain.model.QuizHistory
import com.example.quizapp.domain.model.QuizResult
import kotlinx.coroutines.flow.Flow

interface QuizRepository {
    suspend fun generateQuiz(topic: String, questionCount: Int): Result<List<Question>>
    suspend fun evaluateQuiz(
        questions: List<Question>,
        selectedAnswers: Map<Int, String>
    ): Result<QuizResult>
    suspend fun saveQuizHistory(
        userId: Int,
        topic: String,
        questions: List<Question>,
        selectedAnswers: Map<Int, String>,
        result: QuizResult
    ): Result<Unit>
    fun getQuizHistoryByUser(userId: Int): Flow<List<QuizHistory>>
    suspend fun getQuizHistoryById(quizId: Int): QuizHistory?
    suspend fun deleteQuizHistory(quizId: Int): Result<Unit>
    suspend fun getQuizCount(userId: Int): Int
    suspend fun getAverageScore(userId: Int): Float
}
