package com.github.bobryanskiy.practice.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
fun SqlConstructorTaskContent(
    onBack: () -> Unit,
    task: Task.SqlConstructorTask,
    viewModel: TaskViewModel
) {
    val slots by viewModel.slots.collectAsState()
    val availableParts by viewModel.availableParts.collectAsState()

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

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            slots.forEachIndexed { index, slotContent ->
                SlotBox(
                    content = slotContent,
                    onClick = {
                        if (slotContent.isNotEmpty()) {
                            viewModel.removePartFromSlot(index)
                        } else {
                            val firstAvailable = availableParts.firstOrNull()
                            if (firstAvailable != null) {
                                viewModel.movePartToSlot(firstAvailable, index)
                            }
                        }
                    }
                )
            }
        }

        Text("Доступные части:", style = MaterialTheme.typography.titleSmall)

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            availableParts.forEach { part ->
                DraggablePartBox(
                    part = part,
                    onClick = {
                        val emptyIndex = slots.indexOfFirst { it.isEmpty() }
                        if (emptyIndex != -1) {
                            viewModel.movePartToSlot(part, emptyIndex)
                        }
                    }
                )
            }
        }

        Button(
            onClick = { viewModel.submitSqlConstructorAnswer() }
        ) {
            Text("Проверить")
        }

        if (resultMessage.isNotEmpty()) Text(resultMessage)

        Button(onClick = onBack) {
            Text("Назад")
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun DraggablePartBox(
    part: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .size(70.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = part,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun SlotBox(
    content: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .size(70.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (content.isEmpty()) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = content.ifEmpty { "?" },
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}