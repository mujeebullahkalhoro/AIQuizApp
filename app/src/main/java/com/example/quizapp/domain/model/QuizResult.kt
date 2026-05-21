package com.example.quizapp.domain.model

data class QuizResult(
    val correctCount: Int,
    val wrongCount: Int,
    val score: Float,
    val feedback: String,
    val totalQuestions: Int
) {
    val percentage: Float get() = if (totalQuestions > 0) (correctCount.toFloat() / totalQuestions) * 100f else 0f
}
