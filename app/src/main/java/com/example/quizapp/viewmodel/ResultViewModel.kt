package com.example.quizapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.quizapp.domain.model.Question
import com.example.quizapp.domain.model.QuizResult
import com.example.quizapp.utils.QuizSessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val quizSessionManager: QuizSessionManager
) : ViewModel() {

    private val _result = MutableStateFlow<QuizResult?>(null)
    val result: StateFlow<QuizResult?> = _result.asStateFlow()

    val questions: List<Question> get() = quizSessionManager.questions
    val selectedAnswers: Map<Int, String> get() = quizSessionManager.selectedAnswers
    val topic: String get() = quizSessionManager.currentTopic

    init {
        _result.value = quizSessionManager.currentResult
    }

    fun resetSession() {
        quizSessionManager.clearSession()
    }
}
