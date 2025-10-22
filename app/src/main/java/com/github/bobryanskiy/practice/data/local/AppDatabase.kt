package com.github.bobryanskiy.practice.data.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.github.bobryanskiy.practice.data.local.dao.UsedTableDao
import com.github.bobryanskiy.practice.data.local.dao.UserProgressDao
import com.github.bobryanskiy.practice.data.local.models.UsedTableEntity
import com.github.bobryanskiy.practice.data.local.models.UserProgressEntity

@Database(
    entities = [UserProgressEntity::class, UsedTableEntity::class],
    version = 2,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ]
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProgressDao(): UserProgressDao
    abstract fun usedTableDao(): UsedTableDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sqlgame_database"
                ).addMigrations().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
