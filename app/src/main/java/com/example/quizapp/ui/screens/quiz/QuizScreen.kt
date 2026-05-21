package com.example.quizapp.ui.screens.quiz

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.quizapp.ui.components.FullScreenLoading
import com.example.quizapp.ui.components.OptionItem
import com.example.quizapp.ui.components.QuizTopBar
import com.example.quizapp.utils.UiState
import com.example.quizapp.viewmodel.QuizViewModel

@Composable
fun QuizScreen(
    onNavigateToResult: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: QuizViewModel = hiltViewModel()
) {
    val submitState by viewModel.submitState.collectAsState()
    val currentIndex by viewModel.currentQuestionIndex.collectAsState()
    val selectedAnswers by viewModel.selectedAnswers.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showSubmitDialog by remember { mutableStateOf(false) }

    val questions = viewModel.questions
    val totalQuestions = questions.size

    LaunchedEffect(submitState) {
        when (val state = submitState) {
            is UiState.Success -> {
                viewModel.resetQuizState()
                onNavigateToResult()
            }
            is UiState.Error -> {
                snackbarHostState.showSnackbar(state.message)
                viewModel.resetQuizState()
            }
            else -> {}
        }
    }

    if (totalQuestions == 0) {
        LaunchedEffect(Unit) { onNavigateBack() }
        return
    }

    val isLoading = submitState is UiState.Loading
    val currentQuestion = questions[currentIndex]
    val progress = (currentIndex + 1).toFloat() / totalQuestions
    val optionLabels = listOf("A", "B", "C", "D")
    val answeredCount = selectedAnswers.size

    if (showSubmitDialog) {
        val unanswered = totalQuestions - answeredCount
        AlertDialog(
            onDismissRequest = { showSubmitDialog = false },
            title = {
                Text(
                    text = "Submit Quiz?",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column {
                    if (unanswered > 0) {
                        Text(
                            text = "You have $unanswered unanswered question${if (unanswered > 1) "s" else ""}.",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    Text("Are you sure you want to submit? This action cannot be undone.")
                }
            },
            confirmButton = {
                Button(onClick = {
                    showSubmitDialog = false
                    viewModel.submitQuiz()
                }) { Text("Submit") }
            },
            dismissButton = {
                TextButton(onClick = { showSubmitDialog = false }) { Text("Cancel") }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                QuizTopBar(
                    title = viewModel.topic.ifBlank { "Quiz" },
                    onBackClick = onNavigateBack
                )
            },
            snackbarHost = {
                SnackbarHost(snackbarHostState) { data -> Snackbar(snackbarData = data) }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Question ${currentIndex + 1} of $totalQuestions",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$answeredCount/$totalQuestions answered",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                AnimatedContent(
                    targetState = currentIndex,
                    transitionSpec = {
                        if (targetState > initialState) {
                            slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                        } else {
                            slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
                        }
                    },
                    label = "questionTransition",
                    modifier = Modifier.weight(1f)
                ) { index ->
                    val question = questions[index]
                    val currentSelectedAnswer = selectedAnswers[index]

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                            ),
                            elevation = CardDefaults.cardElevation(0.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(20.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                        fontSize = 14.sp
                                    )
                                }
                                Text(
                                    text = question.question,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Medium,
                                        lineHeight = 26.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        question.options.forEachIndexed { optionIndex, option ->
                            OptionItem(
                                option = option,
                                optionLabel = optionLabels.getOrElse(optionIndex) { "${optionIndex + 1}" },
                                isSelected = currentSelectedAnswer == option,
                                onClick = {
                                    viewModel.selectAnswer(index, option)
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { viewModel.previousQuestion() },
                        enabled = currentIndex > 0,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Previous",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        Text("Previous")
                    }

                    if (currentIndex < totalQuestions - 1) {
                        Button(
                            onClick = { viewModel.nextQuestion() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Next")
                            Spacer(modifier = Modifier.size(6.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Next",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    } else {
                        Button(
                            onClick = { showSubmitDialog = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50)
                            )
                        ) {
                            Text("Submit Quiz", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        if (isLoading) {
            FullScreenLoading(
                message = "Evaluating your answers...",
                subMessage = "Getting personalized feedback"
            )
        }
    }
}
