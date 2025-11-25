package com.example.floramate.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plants")
data class PlantEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val imageUri: String,
    val dateAdded: Long,
    val wateringFrequency: Int, // in days
    val lastWatered: Long,
    val lightPreference: String, // "low", "medium", "high"
    val notes: String? = null,
    val scientificName: String? = null,
    val commonNames: String? = null,
    val careInstructions: String? = null
) {
    fun nextWateringDate(): Long {
        return lastWatered + (wateringFrequency * 24 * 60 * 60 * 1000)
    }

    fun daysUntilWatering(): Int {
        val now = System.currentTimeMillis()
        val next = nextWateringDate()
        val diff = next - now
        return (diff / (24 * 60 * 60 * 1000)).toInt()
    }

    fun needsWatering(): Boolean {
        return System.currentTimeMillis() >= nextWateringDate()
    }
}