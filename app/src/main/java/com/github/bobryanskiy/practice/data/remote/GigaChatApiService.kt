package com.github.bobryanskiy.practice.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface GigaChatApiService {
    @POST("/api/v1/chat/completions")
    suspend fun generateTask(
        @Header("Authorization") bearerToken: String,
        @Body request: GigaChatTaskRequest
    ): Response<GigaChatTaskResponse>
}

data class GigaChatTaskRequest(
    val model: String = "GigaChat",
    val messages: List<GigaChatMessage>,
    val temperature: Float? = null
)

data class GigaChatMessage(
    val role: String,
    val content: String
)

data class GigaChatTaskResponse(
    val choices: List<GigaChatChoice>
)

data class GigaChatChoice(
    val message: GigaChatMessage
)
