package com.github.bobryanskiy.practice.data.remote.dto

data class GigaChatTaskDto(
    val type: String,
    val question: String,
    val options: List<String>?,
    val correct: String?,
    val parts: List<String>?,
    val shuffled_parts: List<String>?,
    val explanation: String?
)