package com.example.floramate.ui.screens.lightmeter

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LightLevel(
    val name: String,
    val description: String,
    val color: Color,
    val minLux: Float,
    val maxLux: Float
)

// --- PERBAIKAN: Fungsi ini dipindah ke sini (Top Level) ---
fun getLightLevelForLux(lux: Float): LightLevel {
    return when {
        lux < 100 -> LightLevel(
            "Very Low Light",
            "Suitable for low-light plants like Snake Plant, Pothos, ZZ Plant",
            Color(0xFF1A237E),
            0f,
            100f
        )
        lux < 500 -> LightLevel(
            "Low Light",
            "Good for shade-tolerant plants like Philodendron, Peace Lily",
            Color(0xFF283593),
            100f,
            500f
        )
        lux < 1000 -> LightLevel(
            "Medium Light",
            "Perfect for most indoor plants like Monstera, Rubber Plant",
            Color(0xFF43A047),
            500f,
            1000f
        )
        lux < 5000 -> LightLevel(
            "Bright Indirect Light",
            "Ideal for many flowering plants and herbs",
            Color(0xFFFDD835),
            1000f,
            5000f
        )
        lux < 10000 -> LightLevel(
            "Very Bright Light",
            "Great for succulents, cacti, and sun-loving plants",
            Color(0xFFFF8F00),
            5000f,
            10000f
        )
        else -> LightLevel(
            "Direct Sunlight",
            "Best for outdoor plants, may be too intense for some indoor plants",
            Color(0xFFD84315),
            10000f,
            Float.MAX_VALUE
        )
    }
}

// Sekarang data class ini bisa memanggil fungsi di atas tanpa error
data class LightMeterUiState(
    val currentLux: Float = 0f,
    val isActive: Boolean = false,
    val lightLevel: LightLevel = getLightLevelForLux(0f), // Error hilang
    val isSensorAvailable: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class LightMeterViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(LightMeterUiState())
    val uiState: StateFlow<LightMeterUiState> = _uiState.asStateFlow()

    private var sensorManager: SensorManager? = null
    private var lightSensor: Sensor? = null
    private var sensorEventListener: SensorEventListener? = null

    fun initSensor(context: Context) {
        viewModelScope.launch {
            sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            lightSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_LIGHT)

            if (lightSensor == null) {
                _uiState.value = _uiState.value.copy(
                    isSensorAvailable = false,
                    error = "Light sensor not available on this device"
                )
                return@launch
            }

            sensorEventListener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    event?.let {
                        val lux = it.values[0]
                        viewModelScope.launch {
                            _uiState.value = _uiState.value.copy(
                                currentLux = lux,
                                lightLevel = getLightLevelForLux(lux) // Error hilang
                            )
                        }
                    }
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                    // Not needed for light sensor
                }
            }
        }
    }

    fun startMeasuring() {
        if (lightSensor == null) {
            _uiState.value = _uiState.value.copy(
                error = "Light sensor not available"
            )
            return
        }

        sensorEventListener?.let {
            sensorManager?.registerListener(
                it,
                lightSensor,
                SensorManager.SENSOR_DELAY_UI
            )
            _uiState.value = _uiState.value.copy(isActive = true, error = null)
        }
    }

    fun stopMeasuring() {
        sensorEventListener?.let {
            sensorManager?.unregisterListener(it)
            _uiState.value = _uiState.value.copy(isActive = false)
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopMeasuring()
    }

    // Companion object dihapus karena isinya sudah dipindah ke atas
}