package com.github.bobryanskiy.practice.data.remote.mapper

import android.util.Log
import com.github.bobryanskiy.practice.data.remote.GigaChatTaskResponse
import com.github.bobryanskiy.practice.data.remote.dto.GigaChatTaskDto
import com.github.bobryanskiy.practice.domain.model.Task
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

fun GigaChatTaskResponse.toDomainTask(): Task {
    val content = this.choices.firstOrNull()?.message?.content ?: ""
//    Log.d("F", content)
    val gson = Gson()

    try {
        val taskDto = gson.fromJson(content, GigaChatTaskDto::class.java)
//        Log.d("F", taskDto.toString())

        return when (taskDto.type) {
            "mcq" -> Task.MultipleChoiceTask(
                id = 1,
                description = taskDto.question,
                options = taskDto.options ?: emptyList(),
                correctAnswer = taskDto.correct ?: "",
                explanation = taskDto.explanation ?: ""
            )
            "sql_constructor" -> Task.SqlConstructorTask(
                id = 2,
                description = taskDto.question,
                parts = taskDto.parts ?: emptyList(),
                shuffledParts = taskDto.shuffled_parts ?: emptyList(),
                explanation = taskDto.explanation ?: ""
            )
            else -> throw IllegalArgumentException("Неизвестный тип задания: ${taskDto.type}")
        }
    } catch (e: JsonSyntaxException) {
        throw Exception("Ошибка парсинга JSON ответа от GigaChat: ${e.message}")
    }
}