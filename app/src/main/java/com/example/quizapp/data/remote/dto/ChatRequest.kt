package com.example.quizapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ChatRequest(
    @SerializedName("model") val model: String,
    @SerializedName("messages") val messages: List<ChatMessage>,
    @SerializedName("temperature") val temperature: Double = 0.7,
    @SerializedName("max_tokens") val maxTokens: Int = 2000
)

data class ChatMessage(
    @SerializedName("role") val role: String,
    @SerializedName("content") val content: String
)
