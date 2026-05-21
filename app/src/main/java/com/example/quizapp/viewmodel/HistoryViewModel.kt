package com.example.quizapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.domain.model.QuizHistory
import com.example.quizapp.domain.usecase.DeleteQuizHistoryUseCase
import com.example.quizapp.domain.usecase.GetQuizHistoryUseCase
import com.example.quizapp.domain.repository.QuizRepository
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
class HistoryViewModel @Inject constructor(
    private val getQuizHistoryUseCase: GetQuizHistoryUseCase,
    private val deleteQuizHistoryUseCase: DeleteQuizHistoryUseCase,
    private val quizRepository: QuizRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _historyState = MutableStateFlow<UiState<List<QuizHistory>>>(UiState.Loading)
    val historyState: StateFlow<UiState<List<QuizHistory>>> = _historyState.asStateFlow()

    private val _selectedQuiz = MutableStateFlow<QuizHistory?>(null)
    val selectedQuiz: StateFlow<QuizHistory?> = _selectedQuiz.asStateFlow()

    private val _deleteState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val deleteState: StateFlow<UiState<Unit>> = _deleteState.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            val userId = sessionManager.userId.firstOrNull() ?: return@launch
            if (userId == -1) return@launch
            getQuizHistoryUseCase(userId).collect { historyList ->
                _historyState.value = UiState.Success(historyList)
            }
        }
    }

    fun loadQuizDetail(quizId: Int) {
        viewModelScope.launch {
            val quiz = quizRepository.getQuizHistoryById(quizId)
            _selectedQuiz.value = quiz
        }
    }

    fun deleteQuiz(quizId: Int) {
        viewModelScope.launch {
            _deleteState.value = UiState.Loading
            val result = deleteQuizHistoryUseCase(quizId)
            result.fold(
                onSuccess = { _deleteState.value = UiState.Success(Unit) },
                onFailure = { throwable ->
                    _deleteState.value = UiState.Error(throwable.message ?: "Delete failed")
                }
            )
        }
    }

    fun resetDeleteState() {
        _deleteState.value = UiState.Idle
    }
}
