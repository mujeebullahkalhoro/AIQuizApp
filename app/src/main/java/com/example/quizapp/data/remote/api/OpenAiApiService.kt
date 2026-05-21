package com.example.quizapp.data.remote.api

import com.example.quizapp.data.remote.dto.ChatRequest
import com.example.quizapp.data.remote.dto.ChatResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OpenAiApiService {

    @POST("chat/completions")
    suspend fun createChatCompletion(
        @Header("Authorization") authorization: String,
        @Body request: ChatRequest
    ): Response<ChatResponse>
}
