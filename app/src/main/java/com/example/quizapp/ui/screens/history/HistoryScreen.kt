package com.example.quizapp.ui.screens.history

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.quizapp.domain.model.QuizHistory
import com.example.quizapp.ui.components.HistoryCard
import com.example.quizapp.ui.components.LoadingView
import com.example.quizapp.ui.components.QuizTopBar
import com.example.quizapp.utils.UiState
import com.example.quizapp.viewmodel.HistoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onNavigateBack: () -> Unit,
    onOpenDetail: (Int) -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val historyState by viewModel.historyState.collectAsState()
    var quizToDelete by remember { mutableStateOf<QuizHistory?>(null) }

    if (quizToDelete != null) {
        AlertDialog(
            onDismissRequest = { quizToDelete = null },
            title = { Text("Delete Quiz", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete this quiz history? This cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        quizToDelete?.let { viewModel.deleteQuiz(it.id) }
                        quizToDelete = null
                    },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { quizToDelete = null }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            QuizTopBar(
                title = "Quiz History",
                onBackClick = onNavigateBack
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = historyState) {
                is UiState.Loading -> LoadingView(message = "Loading history...")
                is UiState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
                    }
                }
                is UiState.Success -> {
                    val historyList = state.data
                    AnimatedVisibility(
                        visible = historyList.isEmpty(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.History,
                                contentDescription = null,
                                modifier = Modifier.size(80.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No quiz history yet",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Complete a quiz to see\nyour history here",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = historyList.isNotEmpty(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = historyList,
                                key = { it.id }
                            ) { quiz ->
                                val dismissState = rememberSwipeToDismissBoxState(
                                    confirmValueChange = { dismissValue ->
                                        if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                                            quizToDelete = quiz
                                        }
                                        false
                                    }
                                )

                                SwipeToDismissBox(
                                    state = dismissState,
                                    backgroundContent = {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.CenterEnd
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Delete,
                                                contentDescription = "Delete",
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.padding(end = 24.dp)
                                            )
                                        }
                                    },
                                    enableDismissFromStartToEnd = false
                                ) {
                                    HistoryCard(
                                        topic = quiz.topic,
                                        questionCount = quiz.questionCount,
                                        score = quiz.score,
                                        date = quiz.date,
                                        onClick = { onOpenDetail(quiz.id) },
                                        onDelete = { quizToDelete = quiz }
                                    )
                                }
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }
}
