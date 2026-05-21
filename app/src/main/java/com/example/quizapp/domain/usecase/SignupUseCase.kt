package com.example.quizapp.domain.usecase

import com.example.quizapp.domain.model.User
import com.example.quizapp.domain.repository.AuthRepository
import javax.inject.Inject

class SignupUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Result<User> {
        if (name.isBlank()) return Result.failure(IllegalArgumentException("Name cannot be empty"))
        if (name.length < 2) return Result.failure(IllegalArgumentException("Name must be at least 2 characters"))
        if (email.isBlank()) return Result.failure(IllegalArgumentException("Email cannot be empty"))
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return Result.failure(IllegalArgumentException("Invalid email format"))
        if (password.isBlank()) return Result.failure(IllegalArgumentException("Password cannot be empty"))
        if (password.length < 6) return Result.failure(IllegalArgumentException("Password must be at least 6 characters"))
        if (password != confirmPassword) return Result.failure(IllegalArgumentException("Passwords do not match"))
        return authRepository.signup(name.trim(), email.trim().lowercase(), password)
    }
}
