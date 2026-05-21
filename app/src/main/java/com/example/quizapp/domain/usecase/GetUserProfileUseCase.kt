package com.example.quizapp.domain.usecase

import com.example.quizapp.domain.model.User
import com.example.quizapp.domain.repository.AuthRepository
import com.example.quizapp.domain.repository.QuizRepository
import javax.inject.Inject

data class UserProfile(
    val user: User,
    val totalQuizzes: Int,
    val averageScore: Float
)

class GetUserProfileUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val quizRepository: QuizRepository
) {
    suspend operator fun invoke(userId: Int): Result<UserProfile> {
        val user = authRepository.getUserById(userId)
            ?: return Result.failure(IllegalStateException("User not found"))
        val totalQuizzes = quizRepository.getQuizCount(userId)
        val averageScore = quizRepository.getAverageScore(userId)
        return Result.success(UserProfile(user, totalQuizzes, averageScore))
    }
}
