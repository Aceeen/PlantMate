package com.example.floramate.data.repository

import com.example.floramate.data.local.dao.PlantDao
import com.example.floramate.data.local.entity.PlantEntity
import com.example.floramate.data.remote.api.PlantApiService
import com.example.floramate.data.remote.model.PlantIdentifyRequest
import com.example.floramate.data.remote.model.PlantIdentifyResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

sealed class Resource<T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error<T>(val message: String) : Resource<T>()
    class Loading<T> : Resource<T>()
}

@Singleton
class PlantRepository @Inject constructor(
    private val plantDao: PlantDao,
    private val plantApiService: PlantApiService
) {
    // Local operations
    fun getAllPlants(): Flow<List<PlantEntity>> = plantDao.getAllPlants()

    suspend fun getPlantById(id: Int): PlantEntity? = plantDao.getPlantById(id)

    suspend fun insertPlant(plant: PlantEntity): Long = plantDao.insertPlant(plant)

    suspend fun updatePlant(plant: PlantEntity) = plantDao.updatePlant(plant)

    suspend fun deletePlant(plant: PlantEntity) = plantDao.deletePlant(plant)

    fun getPlantsNeedingWater(): Flow<List<PlantEntity>> = plantDao.getPlantsNeedingWater()

    fun getPlantsByLightLevel(lightLevel: String): Flow<List<PlantEntity>> =
        plantDao.getPlantsByLightLevel(lightLevel)

    // Remote operations
    suspend fun identifyPlant(imageBase64: String): Resource<PlantIdentifyResponse> {
        return try {
            val request = PlantIdentifyRequest(images = listOf(imageBase64))
            val response = plantApiService.identifyPlant(request)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }

    suspend fun waterPlant(plantId: Int) {
        val plant = getPlantById(plantId)
        plant?.let {
            val updatedPlant = it.copy(lastWatered = System.currentTimeMillis())
            updatePlant(updatedPlant)
        }
    }
}