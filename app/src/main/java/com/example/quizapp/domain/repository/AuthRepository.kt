package com.example.quizapp.domain.repository

import com.example.quizapp.domain.model.User

interface AuthRepository {
    suspend fun signup(name: String, email: String, password: String): Result<User>
    suspend fun login(email: String, password: String): Result<User>
    suspend fun getUserById(userId: Int): User?
}
