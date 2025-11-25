package com.example.floramate.ui.screens.identify

import android.graphics.Bitmap
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.floramate.data.local.entity.PlantEntity
import com.example.floramate.data.remote.model.PlantSuggestion
import com.example.floramate.data.repository.PlantRepository
import com.example.floramate.data.repository.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import javax.inject.Inject

sealed class IdentifyState {
    object Initial : IdentifyState()
    object Loading : IdentifyState()
    data class Success(val suggestions: List<PlantSuggestion>) : IdentifyState()
    data class Error(val message: String) : IdentifyState()
}

@HiltViewModel
class IdentifyViewModel @Inject constructor(
    private val repository: PlantRepository
) : ViewModel() {

    private val _identifyState = MutableStateFlow<IdentifyState>(IdentifyState.Initial)
    val identifyState: StateFlow<IdentifyState> = _identifyState.asStateFlow()

    private val _capturedImage = MutableStateFlow<Bitmap?>(null)
    val capturedImage: StateFlow<Bitmap?> = _capturedImage.asStateFlow()

    fun identifyPlant(bitmap: Bitmap) {
        viewModelScope.launch {
            _identifyState.value = IdentifyState.Loading
            _capturedImage.value = bitmap

            // Convert bitmap to base64
            val base64Image = bitmapToBase64(bitmap)

            when (val result = repository.identifyPlant(base64Image)) {
                is Resource.Success -> {
                    val suggestions = result.data.result.classification.suggestions
                    _identifyState.value = IdentifyState.Success(suggestions)
                }
                is Resource.Error -> {
                    _identifyState.value = IdentifyState.Error(result.message)
                }
                is Resource.Loading -> {
                    // Already handled
                }
            }
        }
    }

    fun addToGarden(suggestion: PlantSuggestion, imageUri: String) {
        viewModelScope.launch {
            val plant = PlantEntity(
                name = suggestion.details?.commonNames?.firstOrNull() ?: suggestion.name,
                scientificName = suggestion.name,
                imageUri = imageUri,
                dateAdded = System.currentTimeMillis(),
                wateringFrequency = suggestion.details?.watering?.max ?: 7,
                lastWatered = System.currentTimeMillis(),
                lightPreference = parseLightPreference(suggestion.details?.bestLightCondition),
                commonNames = suggestion.details?.commonNames?.joinToString(", "),
                careInstructions = suggestion.details?.bestWatering
            )
            repository.insertPlant(plant)
        }
    }

    fun resetIdentification() {
        _identifyState.value = IdentifyState.Initial
        _capturedImage.value = null
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val byteArray = outputStream.toByteArray()
        return "data:image/jpeg;base64," + Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    private fun parseLightPreference(lightCondition: String?): String {
        return when {
            lightCondition == null -> "medium"
            lightCondition.contains("high", ignoreCase = true) ||
                    lightCondition.contains("bright", ignoreCase = true) ||
                    lightCondition.contains("direct", ignoreCase = true) -> "high"
            lightCondition.contains("low", ignoreCase = true) ||
                    lightCondition.contains("shade", ignoreCase = true) -> "low"
            else -> "medium"
        }
    }
}