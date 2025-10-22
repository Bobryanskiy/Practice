package com.github.bobryanskiy.practice.domain.usecase

import com.github.bobryanskiy.practice.domain.model.Task
import com.github.bobryanskiy.practice.domain.repository.TaskRepository
import javax.inject.Inject

class GetNextTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(topic: String, difficulty: String): Task {
        return repository.getNextTask(topic, difficulty)
    }
}