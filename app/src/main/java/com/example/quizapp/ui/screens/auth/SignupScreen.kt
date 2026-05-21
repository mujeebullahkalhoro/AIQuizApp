package com.example.quizapp.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.quizapp.ui.components.InputField
import com.example.quizapp.ui.components.PrimaryButton
import com.example.quizapp.ui.theme.GradientEnd
import com.example.quizapp.ui.theme.GradientStart
import com.example.quizapp.utils.UiState
import com.example.quizapp.viewmodel.AuthViewModel

@Composable
fun SignupScreen(
    onNavigateToLogin: () -> Unit,
    onSignupSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val signupState by viewModel.signupState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    val isLoading = signupState is UiState.Loading
    val visible = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { visible.value = true }

    LaunchedEffect(signupState) {
        when (val state = signupState) {
            is UiState.Success -> {
                snackbarHostState.showSnackbar("Account created! Please sign in.")
                viewModel.resetSignupState()
                onSignupSuccess()
            }
            is UiState.Error -> {
                snackbarHostState.showSnackbar(state.message)
                viewModel.resetSignupState()
            }
            else -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(
                    Brush.linearGradient(
                        colors = listOf(GradientStart, GradientEnd),
                        start = Offset(0f, 0f),
                        end = Offset(1000f, 500f)
                    )
                )
        )

        AnimatedVisibility(
            visible = visible.value,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(48.dp))

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.2f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Psychology,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = androidx.compose.ui.graphics.Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Create Account",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = androidx.compose.ui.graphics.Color.White
                )
                Text(
                    text = "Join AI Quiz Generator",
                    style = MaterialTheme.typography.bodyMedium,
                    color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.85f)
                )

                Spacer(modifier = Modifier.height(40.dp))

                androidx.compose.material3.Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                    elevation = androidx.compose.material3.CardDefaults.cardElevation(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Sign Up",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        InputField(
                            value = name,
                            onValueChange = { name = it; nameError = null },
                            label = "Full Name",
                            leadingIcon = Icons.Filled.Person,
                            isError = nameError != null,
                            errorMessage = nameError,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                        )

                        InputField(
                            value = email,
                            onValueChange = { email = it; emailError = null },
                            label = "Email Address",
                            leadingIcon = Icons.Filled.Email,
                            isError = emailError != null,
                            errorMessage = emailError,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                        )

                        InputField(
                            value = password,
                            onValueChange = { password = it; passwordError = null },
                            label = "Password",
                            leadingIcon = Icons.Filled.Lock,
                            isPassword = true,
                            isError = passwordError != null,
                            errorMessage = passwordError,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                        )

                        InputField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it; confirmPasswordError = null },
                            label = "Confirm Password",
                            leadingIcon = Icons.Filled.Lock,
                            isPassword = true,
                            isError = confirmPasswordError != null,
                            errorMessage = confirmPasswordError,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        PrimaryButton(
                            text = "Create Account",
                            onClick = {
                                var hasError = false
                                if (name.isBlank()) { nameError = "Name is required"; hasError = true }
                                if (email.isBlank()) { emailError = "Email is required"; hasError = true }
                                if (password.isBlank()) { passwordError = "Password is required"; hasError = true }
                                if (confirmPassword.isBlank()) { confirmPasswordError = "Please confirm password"; hasError = true }
                                if (!hasError) viewModel.signup(name, email, password, confirmPassword)
                            },
                            isLoading = isLoading
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Already have an account?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    TextButton(onClick = onNavigateToLogin) {
                        Text(
                            text = "Sign In",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 8.dp)
        ) { data ->
            Snackbar(snackbarData = data)
        }
    }
}
