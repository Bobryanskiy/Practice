package com.github.bobryanskiy.practice.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey val userId: Int = 1,
    val level: Int = 1,
    val points: Int = 0,
    val completedTasks: List<Int> = emptyList()
)