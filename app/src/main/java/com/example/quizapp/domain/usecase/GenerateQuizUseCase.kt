package com.example.quizapp.domain.usecase

import com.example.quizapp.domain.model.Question
import com.example.quizapp.domain.repository.QuizRepository
import javax.inject.Inject

class GenerateQuizUseCase @Inject constructor(
    private val quizRepository: QuizRepository
) {
    suspend operator fun invoke(topic: String, questionCount: Int): Result<List<Question>> {
        if (topic.isBlank()) return Result.failure(IllegalArgumentException("Topic cannot be empty"))
        if (questionCount < 1) return Result.failure(IllegalArgumentException("Question count must be at least 1"))
        if (questionCount > 50) return Result.failure(IllegalArgumentException("Question count cannot exceed 50"))
        return quizRepository.generateQuiz(topic.trim(), questionCount)
    }
}
