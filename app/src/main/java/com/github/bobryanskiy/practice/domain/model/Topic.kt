package com.github.bobryanskiy.practice.domain.model

import androidx.compose.ui.graphics.vector.ImageVector

data class Topic(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val difficulties: List<Difficulty> = Difficulty.entries.toList()
)