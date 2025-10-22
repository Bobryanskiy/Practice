package com.github.bobryanskiy.practice.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.bobryanskiy.practice.data.local.models.UserProgressEntity

@Dao
interface UserProgressDao {
    @Query("SELECT * FROM user_progress WHERE userId = :userId LIMIT 1")
    suspend fun getUserProgress(userId: Int = 1): UserProgressEntity?

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun upsertUserProgress(entity: UserProgressEntity)
}