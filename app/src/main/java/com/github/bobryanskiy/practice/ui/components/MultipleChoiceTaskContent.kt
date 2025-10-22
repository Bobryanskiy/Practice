package com.github.bobryanskiy.practice.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.bobryanskiy.practice.domain.model.Task
import com.github.bobryanskiy.practice.ui.task.TaskViewModel

@Composable
fun MultipleChoiceTaskContent(
    onBack: () -> Unit,
    task: Task.MultipleChoiceTask,
    viewModel: TaskViewModel
) {
    val selectedOption by viewModel.selectedOption.collectAsState()
    val resultMessage by viewModel.resultMessage.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = task.description,
            style = MaterialTheme.typography.headlineSmall
        )

        task.options.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.selectOption(option) }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedOption == option,
                    onClick = null
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = option,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Button(
            onClick = { viewModel.submitMCQAnswer() }
        ) {
            Text("Ответить")
        }

        if (resultMessage.isNotEmpty()) Text(resultMessage)

        Button(onClick = onBack) {
            Text("Назад")
        }
    }
}