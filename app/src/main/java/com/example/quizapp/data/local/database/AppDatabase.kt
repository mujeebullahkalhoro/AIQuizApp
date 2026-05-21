package com.example.quizapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.quizapp.data.local.dao.QuizHistoryDao
import com.example.quizapp.data.local.dao.UserDao
import com.example.quizapp.data.local.entities.QuizHistoryEntity
import com.example.quizapp.data.local.entities.UserEntity

@Database(
    entities = [UserEntity::class, QuizHistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun quizHistoryDao(): QuizHistoryDao
}
