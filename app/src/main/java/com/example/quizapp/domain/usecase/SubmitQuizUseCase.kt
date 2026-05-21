package com.example.quizapp.domain.usecase

import com.example.quizapp.domain.model.Question
import com.example.quizapp.domain.model.QuizResult
import com.example.quizapp.domain.repository.QuizRepository
import javax.inject.Inject

class SubmitQuizUseCase @Inject constructor(
    private val quizRepository: QuizRepository
) {
    suspend operator fun invoke(
        questions: List<Question>,
        selectedAnswers: Map<Int, String>
    ): Result<QuizResult> {
        if (questions.isEmpty()) return Result.failure(IllegalArgumentException("No questions to evaluate"))
        return quizRepository.evaluateQuiz(questions, selectedAnswers)
    }
}
