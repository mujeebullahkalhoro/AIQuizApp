package com.example.quizapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.domain.usecase.GetUserProfileUseCase
import com.example.quizapp.domain.usecase.UserProfile
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
class ProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _profileState = MutableStateFlow<UiState<UserProfile>>(UiState.Loading)
    val profileState: StateFlow<UiState<UserProfile>> = _profileState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            val userId = sessionManager.userId.firstOrNull() ?: return@launch
            if (userId == -1) return@launch
            val result = getUserProfileUseCase(userId)
            result.fold(
                onSuccess = { profile ->
                    _profileState.value = UiState.Success(profile)
                },
                onFailure = { throwable ->
                    _profileState.value = UiState.Error(throwable.message ?: "Failed to load profile")
                }
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            sessionManager.clearSession()
        }
    }

    fun refreshProfile() {
        _profileState.value = UiState.Loading
        loadProfile()
    }
}
