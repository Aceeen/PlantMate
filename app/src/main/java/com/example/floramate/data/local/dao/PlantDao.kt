package com.example.floramate.data.local.dao

import androidx.room.*
import com.example.floramate.data.local.entity.PlantEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {
    @Query("SELECT * FROM plants ORDER BY dateAdded DESC")
    fun getAllPlants(): Flow<List<PlantEntity>>

    @Query("SELECT * FROM plants WHERE id = :id")
    suspend fun getPlantById(id: Int): PlantEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlant(plant: PlantEntity): Long

    @Update
    suspend fun updatePlant(plant: PlantEntity)

    @Delete
    suspend fun deletePlant(plant: PlantEntity)

    @Query("SELECT * FROM plants WHERE lastWatered + (wateringFrequency * 24 * 60 * 60 * 1000) <= :currentTime")
    fun getPlantsNeedingWater(currentTime: Long = System.currentTimeMillis()): Flow<List<PlantEntity>>

    @Query("SELECT * FROM plants WHERE lightPreference = :lightLevel")
    fun getPlantsByLightLevel(lightLevel: String): Flow<List<PlantEntity>>
}