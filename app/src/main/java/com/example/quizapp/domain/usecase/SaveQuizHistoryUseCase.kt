package com.example.quizapp.domain.usecase

import com.example.quizapp.domain.model.Question
import com.example.quizapp.domain.model.QuizResult
import com.example.quizapp.domain.repository.QuizRepository
import javax.inject.Inject

class SaveQuizHistoryUseCase @Inject constructor(
    private val quizRepository: QuizRepository
) {
    suspend operator fun invoke(
        userId: Int,
        topic: String,
        questions: List<Question>,
        selectedAnswers: Map<Int, String>,
        result: QuizResult
    ): Result<Unit> {
        return quizRepository.saveQuizHistory(userId, topic, questions, selectedAnswers, result)
    }
}
