package com.example.floramate.ui.screens.home

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

data class HomeUiState(
    val recentPlants: List<PlantEntity> = emptyList(),
    val plantsNeedingWater: List<PlantEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: PlantRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                // Load recent plants
                repository.getAllPlants().collect { plants ->
                    _uiState.value = _uiState.value.copy(
                        recentPlants = plants.take(5),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message,
                    isLoading = false
                )
            }
        }

        viewModelScope.launch {
            try {
                // Load plants needing water
                repository.getPlantsNeedingWater().collect { plants ->
                    _uiState.value = _uiState.value.copy(
                        plantsNeedingWater = plants
                    )
                }
            } catch (e: Exception) {
                // Handle error silently for this
            }
        }
    }

    fun refresh() {
        loadData()
    }
}