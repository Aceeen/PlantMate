package com.example.floramate.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.floramate.data.local.dao.PlantDao
import com.example.floramate.data.local.entity.PlantEntity

@Database(
    entities = [PlantEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PlantDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao
}