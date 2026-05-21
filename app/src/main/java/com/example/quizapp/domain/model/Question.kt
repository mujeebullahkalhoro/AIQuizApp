package com.example.quizapp.domain.model

data class Question(
    val question: String,
    val options: List<String>,
    val correctAnswer: String
)
