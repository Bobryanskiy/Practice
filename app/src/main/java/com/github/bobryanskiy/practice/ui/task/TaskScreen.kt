package com.github.bobryanskiy.practice.ui.task

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.github.bobryanskiy.practice.domain.model.Task
import com.github.bobryanskiy.practice.ui.components.MultipleChoiceTaskContent
import com.github.bobryanskiy.practice.ui.components.SqlConstructorTaskContent

@Composable
fun TaskScreen(onBack: () -> Unit) {
    val viewModel: TaskViewModel = hiltViewModel()

    val currentTask by viewModel.currentTask.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.error.collectAsState()

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CircularProgressIndicator()
                Text("Загрузка задания...")
            }
        }
        return
    }

    errorMessage?.let { error ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Ошибка: $error", color = MaterialTheme.colorScheme.error)
                Button(onClick = { viewModel.loadNextTask() }) {
                    Text("Повторить")
                }
                Button(onClick = onBack) {
                    Text("Назад")
                }
            }
        }
        return
    }

    when (val task = currentTask) {
        is Task.MultipleChoiceTask -> {
            MultipleChoiceTaskContent(onBack, task, viewModel)
        }
        is Task.SqlConstructorTask -> {
            SqlConstructorTaskContent(onBack, task, viewModel)
        }
        null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Не удалось загрузить задание")
                    Button(onClick = { viewModel.loadNextTask() }) {
                        Text("Попробовать снова")
                    }
                    Button(onClick = onBack) {
                        Text("Назад")
                    }
                }
            }
        }
    }
}