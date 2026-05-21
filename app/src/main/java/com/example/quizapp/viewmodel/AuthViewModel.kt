package com.example.quizapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.domain.model.User
import com.example.quizapp.domain.usecase.LoginUseCase
import com.example.quizapp.domain.usecase.SignupUseCase
import com.example.quizapp.utils.SessionManager
import com.example.quizapp.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val signupUseCase: SignupUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _loginState = MutableStateFlow<UiState<User>>(UiState.Idle)
    val loginState: StateFlow<UiState<User>> = _loginState.asStateFlow()

    private val _signupState = MutableStateFlow<UiState<User>>(UiState.Idle)
    val signupState: StateFlow<UiState<User>> = _signupState.asStateFlow()

    val isLoggedIn = sessionManager.isLoggedIn
    val userId = sessionManager.userId

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = UiState.Loading
            val result = loginUseCase(email, password)
            result.fold(
                onSuccess = { user ->
                    sessionManager.saveSession(user.id)
                    _loginState.value = UiState.Success(user)
                },
                onFailure = { throwable ->
                    _loginState.value = UiState.Error(throwable.message ?: "Login failed")
                }
            )
        }
    }

    fun signup(name: String, email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            _signupState.value = UiState.Loading
            val result = signupUseCase(name, email, password, confirmPassword)
            result.fold(
                onSuccess = { user ->
                    _signupState.value = UiState.Success(user)
                },
                onFailure = { throwable ->
                    _signupState.value = UiState.Error(throwable.message ?: "Signup failed")
                }
            )
        }
    }

    fun resetLoginState() {
        _loginState.value = UiState.Idle
    }

    fun resetSignupState() {
        _signupState.value = UiState.Idle
    }
}
