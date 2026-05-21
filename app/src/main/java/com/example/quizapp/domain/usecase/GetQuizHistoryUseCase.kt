package com.example.quizapp.domain.usecase

import com.example.quizapp.domain.model.QuizHistory
import com.example.quizapp.domain.repository.QuizRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetQuizHistoryUseCase @Inject constructor(
    private val quizRepository: QuizRepository
) {
    operator fun invoke(userId: Int): Flow<List<QuizHistory>> {
        return quizRepository.getQuizHistoryByUser(userId)
    }
}
