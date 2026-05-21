package com.example.quizapp.utils

import com.example.quizapp.domain.model.Question
import com.example.quizapp.domain.model.QuizResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizSessionManager @Inject constructor() {
    private var _questions: List<Question> = emptyList()
    val questions: List<Question> get() = _questions

    private var _selectedAnswers: MutableMap<Int, String> = mutableMapOf()
    val selectedAnswers: Map<Int, String> get() = _selectedAnswers

    var currentTopic: String = ""
    var currentResult: QuizResult? = null

    fun startSession(topic: String, questions: List<Question>) {
        currentTopic = topic
        _questions = questions
        _selectedAnswers = mutableMapOf()
        currentResult = null
    }

    fun setAnswer(questionIndex: Int, answer: String) {
        _selectedAnswers[questionIndex] = answer
    }

    fun removeAnswer(questionIndex: Int) {
        _selectedAnswers.remove(questionIndex)
    }

    fun setResult(result: QuizResult) {
        currentResult = result
    }

    fun clearSession() {
        _questions = emptyList()
        _selectedAnswers = mutableMapOf()
        currentResult = null
        currentTopic = ""
    }

    fun isAnswerSelected(questionIndex: Int): Boolean = _selectedAnswers.containsKey(questionIndex)

    fun getAnswer(questionIndex: Int): String? = _selectedAnswers[questionIndex]

    val answeredCount: Int get() = _selectedAnswers.size
}
