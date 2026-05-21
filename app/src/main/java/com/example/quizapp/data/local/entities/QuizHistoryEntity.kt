package com.example.quizapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_history")
data class QuizHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val topic: String,
    val questionCount: Int,
    val correctAnswers: Int,
    val wrongAnswers: Int,
    val score: Float,
    val feedback: String,
    val date: String,
    val questionsJson: String,
    val selectedAnswersJson: String
)
