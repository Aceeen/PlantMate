package com.example.floramate.ui.screens.garden

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.floramate.data.local.entity.PlantEntity
import com.example.floramate.data.repository.PlantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GardenUiState(
    val plants: List<PlantEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val filterBy: FilterType = FilterType.ALL
)

enum class FilterType {
    ALL,
    NEEDS_WATER,
    LOW_LIGHT,
    MEDIUM_LIGHT,
    HIGH_LIGHT
}

@HiltViewModel
class VirtualGardenViewModel @Inject constructor(
    private val repository: PlantRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GardenUiState())
    val uiState: StateFlow<GardenUiState> = _uiState.asStateFlow()

    private var allPlants: List<PlantEntity> = emptyList()

    init {
        loadPlants()
    }

    private fun loadPlants() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                repository.getAllPlants().collect { plantList ->
                    allPlants = plantList
                    applyFilter(_uiState.value.filterBy)
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message,
                    isLoading = false
                )
            }
        }
    }

    fun waterPlant(plantId: Int) {
        viewModelScope.launch {
            try {
                repository.waterPlant(plantId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun deletePlant(plant: PlantEntity) {
        viewModelScope.launch {
            try {
                repository.deletePlant(plant)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun setFilter(filterType: FilterType) {
        _uiState.value = _uiState.value.copy(filterBy = filterType)
        applyFilter(filterType)
    }

    private fun applyFilter(filterType: FilterType) {
        val filteredPlants = when (filterType) {
            FilterType.ALL -> allPlants
            FilterType.NEEDS_WATER -> allPlants.filter { it.needsWatering() }
            FilterType.LOW_LIGHT -> allPlants.filter { it.lightPreference == "low" }
            FilterType.MEDIUM_LIGHT -> allPlants.filter { it.lightPreference == "medium" }
            FilterType.HIGH_LIGHT -> allPlants.filter { it.lightPreference == "high" }
        }

        _uiState.value = _uiState.value.copy(plants = filteredPlants)
    }

    fun refresh() {
        loadPlants()
    }
}