package com.example.quizapp.domain.usecase

import com.example.quizapp.domain.model.User
import com.example.quizapp.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        if (email.isBlank()) return Result.failure(IllegalArgumentException("Email cannot be empty"))
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return Result.failure(IllegalArgumentException("Invalid email format"))
        if (password.isBlank()) return Result.failure(IllegalArgumentException("Password cannot be empty"))
        if (password.length < 6) return Result.failure(IllegalArgumentException("Password must be at least 6 characters"))
        return authRepository.login(email.trim(), password)
    }
}
