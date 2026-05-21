package com.example.quizapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.domain.model.Question
import com.example.quizapp.domain.usecase.GenerateQuizUseCase
import com.example.quizapp.utils.QuizSessionManager
import com.example.quizapp.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val generateQuizUseCase: GenerateQuizUseCase,
    private val quizSessionManager: QuizSessionManager
) : ViewModel() {

    private val _generateState = MutableStateFlow<UiState<List<Question>>>(UiState.Idle)
    val generateState: StateFlow<UiState<List<Question>>> = _generateState.asStateFlow()

    private val _topic = MutableStateFlow("")
    val topic: StateFlow<String> = _topic.asStateFlow()

    private val _questionCount = MutableStateFlow(10)
    val questionCount: StateFlow<Int> = _questionCount.asStateFlow()

    fun updateTopic(topic: String) {
        _topic.value = topic
    }

    fun updateQuestionCount(count: Int) {
        _questionCount.value = count
    }

    fun generateQuiz() {
        val currentTopic = _topic.value
        val count = _questionCount.value
        viewModelScope.launch {
            _generateState.value = UiState.Loading
            val result = generateQuizUseCase(currentTopic, count)
            result.fold(
                onSuccess = { questions ->
                    quizSessionManager.startSession(currentTopic, questions)
                    _generateState.value = UiState.Success(questions)
                },
                onFailure = { throwable ->
                    _generateState.value = UiState.Error(throwable.message ?: "Failed to generate quiz")
                }
            )
        }
    }

    fun resetState() {
        _generateState.value = UiState.Idle
    }
}
