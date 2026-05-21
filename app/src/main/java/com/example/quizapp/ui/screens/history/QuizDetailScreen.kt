package com.example.quizapp.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.quizapp.ui.components.LoadingView
import com.example.quizapp.ui.components.QuizTopBar
import com.example.quizapp.ui.theme.GradientEnd
import com.example.quizapp.ui.theme.GradientStart
import com.example.quizapp.viewmodel.HistoryViewModel

@Composable
fun QuizDetailScreen(
    quizId: Int,
    onNavigateBack: () -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val selectedQuiz by viewModel.selectedQuiz.collectAsState()

    LaunchedEffect(quizId) {
        viewModel.loadQuizDetail(quizId)
    }

    val quiz = selectedQuiz

    Scaffold(
        topBar = {
            QuizTopBar(
                title = quiz?.topic ?: "Quiz Detail",
                onBackClick = onNavigateBack
            )
        }
    ) { paddingValues ->
        if (quiz == null) {
            LoadingView(modifier = Modifier.padding(paddingValues))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    // Summary Header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(GradientStart, GradientEnd),
                                    start = Offset(0f, 0f),
                                    end = Offset(800f, 200f)
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Text(
                                text = quiz.topic,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = quiz.date,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                                SummaryItem("Score", "${quiz.score.toInt()}%", Color.White)
                                SummaryItem("Correct", quiz.correctAnswers.toString(), Color(0xFFB9F6CA))
                                SummaryItem("Wrong", quiz.wrongAnswers.toString(), Color(0xFFFFCDD2))
                                SummaryItem("Total", quiz.questionCount.toString(), Color.White.copy(alpha = 0.8f))
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "Question Review",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                itemsIndexed(quiz.questions) { index, question ->
                    val selectedAnswer = quiz.selectedAnswers[index] ?: ""
                    val isCorrect = selectedAnswer.trim().equals(question.correctAnswer.trim(), ignoreCase = true)
                    val optionLabels = listOf("A", "B", "C", "D")

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(2.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCorrect)
                                Color(0xFF4CAF50).copy(alpha = 0.05f)
                            else
                                Color(0xFFF44336).copy(alpha = 0.05f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontSize = 12.sp
                                    )
                                }
                                Icon(
                                    imageVector = if (isCorrect) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                                    contentDescription = null,
                                    tint = if (isCorrect) Color(0xFF4CAF50) else Color(0xFFF44336),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = question.question,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            question.options.forEachIndexed { optIndex, option ->
                                val isThisCorrect = option.trim().equals(question.correctAnswer.trim(), ignoreCase = true)
                                val isThisSelected = option.trim().equals(selectedAnswer.trim(), ignoreCase = true)
                                val bgColor = when {
                                    isThisCorrect -> Color(0xFF4CAF50).copy(alpha = 0.15f)
                                    isThisSelected && !isThisCorrect -> Color(0xFFF44336).copy(alpha = 0.15f)
                                    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                }
                                val borderColor = when {
                                    isThisCorrect -> Color(0xFF4CAF50)
                                    isThisSelected && !isThisCorrect -> Color(0xFFF44336)
                                    else -> Color.Transparent
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(bgColor)
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .background(
                                                if (isThisSelected || isThisCorrect)
                                                    if (isThisCorrect) Color(0xFF4CAF50) else Color(0xFFF44336)
                                                else MaterialTheme.colorScheme.surfaceVariant,
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = optionLabels.getOrElse(optIndex) { "${optIndex + 1}" },
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = if (isThisSelected || isThisCorrect) Color.White
                                            else MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 11.sp
                                        )
                                    }
                                    Text(
                                        text = option,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (isThisCorrect) {
                                        Icon(
                                            imageVector = Icons.Filled.CheckCircle,
                                            contentDescription = "Correct",
                                            tint = Color(0xFF4CAF50),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
private fun SummaryItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = color,
            fontSize = 18.sp
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color.copy(alpha = 0.8f)
        )
    }
}
