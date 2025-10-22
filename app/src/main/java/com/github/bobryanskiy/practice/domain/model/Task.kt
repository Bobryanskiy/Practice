package com.github.bobryanskiy.practice.domain.model

sealed class Task {
    abstract val id: Int
    abstract val description: String

    data class MultipleChoiceTask(
        override val id: Int,
        override val description: String,
        val options: List<String>,
        val correctAnswer: String,
        val explanation: String
    ) : Task()

    data class SqlConstructorTask(
        override val id: Int,
        override val description: String,
        val parts: List<String>,
        val shuffledParts: List<String>,
        val explanation: String
    ) : Task()
}