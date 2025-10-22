package com.github.bobryanskiy.practice.data.local.models

import androidx.room.Entity

@Entity(tableName = "used_tables", primaryKeys = ["topic", "difficulty", "tableName"])
data class UsedTableEntity(
    val topic: String,
    val difficulty: String,
    val tableName: String
)