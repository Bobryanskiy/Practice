package com.github.bobryanskiy.practice.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Link

val TOPICS = listOf(
    Topic(
        id = "select",
        title = "Основы SELECT",
        icon = Icons.AutoMirrored.Filled.ViewList,
        difficulties = Difficulty.entries
    ),
    Topic(
        id = "where",
        title = "Фильтрация (WHERE)",
        icon = Icons.Default.FilterAlt,
        difficulties = Difficulty.entries
    ),
    Topic(
        id = "sorting",
        title = "Сортировка и LIMIT",
        icon = Icons.AutoMirrored.Filled.Sort,
        difficulties = Difficulty.entries
    ),
    Topic(
        id = "aggregates",
        title = "Агрегаты (COUNT, SUM)",
        icon = Icons.Default.BarChart,
        difficulties = Difficulty.entries
    ),
    Topic(
        id = "group_by",
        title = "Группировка (GROUP BY)",
        icon = Icons.Default.Group,
        difficulties = Difficulty.entries
    ),
    Topic(
        id = "joins",
        title = "Связи (JOIN)",
        icon = Icons.Default.Link,
        difficulties = Difficulty.entries
    ),
    Topic(
        id = "subqueries",
        title = "Подзапросы",
        icon = Icons.Default.Autorenew,
        difficulties = Difficulty.entries
    )
)