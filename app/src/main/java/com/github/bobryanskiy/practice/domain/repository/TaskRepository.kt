package com.github.bobryanskiy.practice.domain.repository

import com.github.bobryanskiy.practice.domain.model.Task

interface TaskRepository {
    suspend fun getNextTask(topic: String, difficulty: String): Task
}