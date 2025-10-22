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

    private val _slots = MutableStateFlow<Array<String>>(arrayOf())
    val slots: StateFlow<Array<String>> = _slots

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

    val route = savedStateHandle.toRoute<com.github.bobryanskiy.practice.ui.Task>()
    private val topic = route.topic
    private val difficulty= route.difficulty

    init {
        loadNextTask()
    }

    private fun resetStateForNewTask(task: Task) {
        _selectedOption.value = null
        _resultMessage.value = ""

        if (task is Task.SqlConstructorTask) {
            _slots.value = Array(task.parts.size) { "" }
            _availableParts.value = task.shuffledParts.toMutableList()
        }
    }

    fun loadNextTask() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val task = getNextTaskUseCase(topic, difficulty)
                _currentTask.value = task
                resetStateForNewTask(task)
            } catch (e: Exception) {
//                _resultMessage.value = "Ошибка загрузки задания: ${e.message}"
                Log.e("F", e.message.toString())
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

        if (slotIndex < currentSlots.size && currentSlots[slotIndex].isEmpty() && currentAvailable.contains(part)) {
            val newSlots = currentSlots.copyOf()
            newSlots[slotIndex] = part
            _slots.value = newSlots

            currentAvailable.remove(part)
            _availableParts.value = currentAvailable
        }
    }

    fun removePartFromSlot(slotIndex: Int) {
        val currentSlots = _slots.value
        val currentAvailable = _availableParts.value.toMutableList()

        if (slotIndex < currentSlots.size) {
            val part = currentSlots[slotIndex]
            if (part.isNotEmpty()) {
                val newSlots = currentSlots.copyOf()
                newSlots[slotIndex] = ""
                _slots.value = newSlots

                currentAvailable.add(part)
                _availableParts.value = currentAvailable
            }
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
