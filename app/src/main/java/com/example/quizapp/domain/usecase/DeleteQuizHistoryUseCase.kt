package com.example.quizapp.domain.usecase

import com.example.quizapp.domain.repository.QuizRepository
import javax.inject.Inject

class DeleteQuizHistoryUseCase @Inject constructor(
    private val quizRepository: QuizRepository
) {
    suspend operator fun invoke(quizId: Int): Result<Unit> {
        return quizRepository.deleteQuizHistory(quizId)
    }
}
