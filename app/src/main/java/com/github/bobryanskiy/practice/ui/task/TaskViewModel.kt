package com.github.bobryanskiy.practice.ui.task

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.github.bobryanskiy.practice.domain.model.Task
import com.github.bobryanskiy.practice.domain.usecase.GetNextTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class TaskViewModel @Inject constructor(savedStateHandle: SavedStateHandle, private val getNextTaskUseCase: GetNextTaskUseCase) : ViewModel() {
    private val _currentTask = MutableStateFlow<Task?>(null)
    val currentTask: StateFlow<Task?> = _currentTask

    private val _slots = MutableStateFlow<List<String>>(emptyList())
    val slots: StateFlow<List<String>> = _slots

    private val _availableParts = MutableStateFlow<List<String>>(emptyList())
    val availableParts: StateFlow<List<String>> = _availableParts

    private val _selectedOption = MutableStateFlow<String?>(null)
    val selectedOption: StateFlow<String?> = _selectedOption

    private val _resultMessage = MutableStateFlow("")
    val resultMessage: StateFlow<String> = _resultMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isAnswerSubmitted = MutableStateFlow(false)
    val isAnswerSubmitted: StateFlow<Boolean> = _isAnswerSubmitted

    val route = savedStateHandle.toRoute<com.github.bobryanskiy.practice.ui.Task>()
    private val topic = route.topic
    private val difficulty = route.difficulty

    init {
        loadNextTask()
    }

    private fun resetStateForNewTask(task: Task) {
        _selectedOption.value = null
        _resultMessage.value = ""

        if (task is Task.SqlConstructorTask) {
            _slots.value = List(task.parts.size) { "" }
            _availableParts.value = task.shuffledParts.toMutableList()
        }
    }

    fun loadNextTask() {
        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val task = getNextTaskUseCase(topic, difficulty)
                _currentTask.value = task
                resetStateForNewTask(task)
            } catch (e: Exception) {
                Log.e("TaskViewModel", "Failed to load task", e)
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectOption(option: String) {
        _selectedOption.value = option
    }

    fun submitMCQAnswer() {
        viewModelScope.launch {
            val task = _currentTask.value as? Task.MultipleChoiceTask ?: return@launch
            val selected = _selectedOption.value

            if (selected != null) {
                _resultMessage.value = if (selected == task.correctAnswer) "Правильно!" else "Неправильно. Правильный ответ:\n${task.correctAnswer}\n\nОбъяснение: ${task.explanation}"
                if (selected == task.correctAnswer) {
                    loadNextTask()
                }
            }
        }
    }

    fun movePartToSlot(part: String, slotIndex: Int) {
        val currentSlots = _slots.value
        val currentAvailable = _availableParts.value.toMutableList()

        if (slotIndex in currentSlots.indices && currentSlots[slotIndex].isEmpty() && part in currentAvailable) {
            val newSlots = _slots.value.toMutableList().apply { this[slotIndex] = part }
            _slots.value = newSlots
            _availableParts.value = currentAvailable - part
        }
    }

    fun removePartFromSlot(slotIndex: Int) {
        val currentSlots = _slots.value
        if (slotIndex !in currentSlots.indices) return

        val part = currentSlots[slotIndex]
        if (part.isNotEmpty()) {
            val newSlots = currentSlots.toMutableList().apply { this[slotIndex] = "" }
            _slots.value = newSlots

            _availableParts.value = _availableParts.value + part
        }
    }

    fun submitSqlConstructorAnswer() {
        viewModelScope.launch {
            val task = _currentTask.value as? Task.SqlConstructorTask ?: return@launch
            val correctAnswer = task.parts.joinToString(" ")
            val userAnswer = _slots.value.filter { it.isNotEmpty() }.joinToString(" ")

            _resultMessage.value = if (userAnswer == correctAnswer) {
                "Правильно!"
            } else {
                "Неправильно. Правильный ответ:\n$correctAnswer\n\nОбъяснение: ${task.explanation}"
            }

            if (userAnswer == correctAnswer) {
                loadNextTask()
            }
        }
    }
}
