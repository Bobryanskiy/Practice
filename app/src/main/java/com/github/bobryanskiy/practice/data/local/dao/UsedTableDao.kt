package com.github.bobryanskiy.practice.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.bobryanskiy.practice.data.local.models.UsedTableEntity

@Dao
interface UsedTableDao {
    @Query("SELECT tableName FROM used_tables WHERE topic = :topic AND difficulty = :difficulty")
    suspend fun getUsedTables(topic: String, difficulty: String): List<String>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(table: UsedTableEntity)

    @Query("DELETE FROM used_tables WHERE topic = :topic AND difficulty = :difficulty")
    suspend fun clearFor(topic: String, difficulty: String)
}