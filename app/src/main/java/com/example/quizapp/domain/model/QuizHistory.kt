package com.example.quizapp.domain.model

data class QuizHistory(
    val id: Int,
    val userId: Int,
    val topic: String,
    val questionCount: Int,
    val correctAnswers: Int,
    val wrongAnswers: Int,
    val score: Float,
    val feedback: String,
    val date: String,
    val questions: List<Question>,
    val selectedAnswers: Map<Int, String>
)
