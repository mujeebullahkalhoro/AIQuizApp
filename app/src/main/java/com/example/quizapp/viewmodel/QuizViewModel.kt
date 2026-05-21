package com.example.quizapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.domain.model.Question
import com.example.quizapp.domain.model.QuizResult
import com.example.quizapp.domain.usecase.SaveQuizHistoryUseCase
import com.example.quizapp.domain.usecase.SubmitQuizUseCase
import com.example.quizapp.utils.QuizSessionManager
import com.example.quizapp.utils.SessionManager
import com.example.quizapp.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val submitQuizUseCase: SubmitQuizUseCase,
    private val saveQuizHistoryUseCase: SaveQuizHistoryUseCase,
    private val quizSessionManager: QuizSessionManager,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _submitState = MutableStateFlow<UiState<QuizResult>>(UiState.Idle)
    val submitState: StateFlow<UiState<QuizResult>> = _submitState.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    // Reactive state for selected answers - triggers recomposition on selection change
    private val _selectedAnswers = MutableStateFlow<Map<Int, String>>(emptyMap())
    val selectedAnswers: StateFlow<Map<Int, String>> = _selectedAnswers.asStateFlow()

    val questions: List<Question> get() = quizSessionManager.questions
    val topic: String get() = quizSessionManager.currentTopic

    fun selectAnswer(questionIndex: Int, answer: String) {
        quizSessionManager.setAnswer(questionIndex, answer)
        _selectedAnswers.value = quizSessionManager.selectedAnswers.toMap()
    }

    fun getSelectedAnswer(questionIndex: Int): String? = _selectedAnswers.value[questionIndex]

    fun isAnswered(questionIndex: Int): Boolean = _selectedAnswers.value.containsKey(questionIndex)

    fun nextQuestion() {
        if (_currentQuestionIndex.value < questions.size - 1) {
            _currentQuestionIndex.value++
        }
    }

    fun previousQuestion() {
        if (_currentQuestionIndex.value > 0) {
            _currentQuestionIndex.value--
        }
    }

    val answeredCount: Int get() = _selectedAnswers.value.size

    fun submitQuiz() {
        viewModelScope.launch {
            _submitState.value = UiState.Loading
            val result = submitQuizUseCase(questions, _selectedAnswers.value)
            result.fold(
                onSuccess = { quizResult ->
                    quizSessionManager.setResult(quizResult)
                    saveHistory(quizResult)
                    _submitState.value = UiState.Success(quizResult)
                },
                onFailure = { throwable ->
                    _submitState.value = UiState.Error(throwable.message ?: "Submission failed")
                }
            )
        }
    }

    private suspend fun saveHistory(result: QuizResult) {
        val userId = sessionManager.userId.firstOrNull() ?: return
        if (userId == -1) return
        saveQuizHistoryUseCase(
            userId = userId,
            topic = quizSessionManager.currentTopic,
            questions = questions,
            selectedAnswers = _selectedAnswers.value,
            result = result
        )
    }

    fun resetQuizState() {
        _submitState.value = UiState.Idle
        _currentQuestionIndex.value = 0
        _selectedAnswers.value = emptyMap()
    }
}
