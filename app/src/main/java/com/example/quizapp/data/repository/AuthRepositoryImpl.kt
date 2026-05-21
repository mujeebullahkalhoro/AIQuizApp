package com.example.quizapp.data.repository

import com.example.quizapp.data.local.dao.UserDao
import com.example.quizapp.data.local.entities.UserEntity
import com.example.quizapp.domain.model.User
import com.example.quizapp.domain.repository.AuthRepository
import com.example.quizapp.utils.hashPassword
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : AuthRepository {

    override suspend fun signup(name: String, email: String, password: String): Result<User> {
        return try {
            val existingCount = userDao.emailExists(email)
            if (existingCount > 0) {
                return Result.failure(IllegalStateException("An account with this email already exists"))
            }
            val entity = UserEntity(
                name = name,
                email = email,
                password = password.hashPassword()
            )
            val id = userDao.insertUser(entity)
            Result.success(User(id.toInt(), name, email))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val entity = userDao.login(email.lowercase(), password.hashPassword())
                ?: return Result.failure(IllegalArgumentException("Invalid email or password"))
            Result.success(User(entity.id, entity.name, entity.email))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserById(userId: Int): User? {
        return try {
            val entity = userDao.getUserById(userId) ?: return null
            User(entity.id, entity.name, entity.email)
        } catch (e: Exception) {
            null
        }
    }
}
