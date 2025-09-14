package com.example.weather_app.ui.weather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_app.domain.model.WeatherEntry
import com.example.weather_app.domain.model.WeatherResponse
import com.example.weather_app.domain.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _weather = MutableLiveData<WeatherResponse>()
    val weather: LiveData<WeatherResponse> get() = _weather

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _history = MutableLiveData<List<WeatherEntry>>()
    val history: LiveData<List<WeatherEntry>> get() = _history

    private var fetchJob: Job? = null

    fun fetchWeather(lat: Double, lon: Double) {
        fetchJob?.cancel()

        fetchJob = viewModelScope.launch {
            try {
                val response = repository.fetchCurrentWeather(lat, lon)
                _weather.value = response

                saveWeather(response)
            } catch (_: CancellationException) {
                // Coroutine was cancelled, ignore
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    private fun saveWeather(response: WeatherResponse) {
        val entry = WeatherEntry(
            city = response.name,
            country = response.sys.country,
            temp = response.main.temp,
            description = response.weather.firstOrNull()?.description ?: "",
            icon = response.weather.firstOrNull()?.icon ?: "01d",
            timestamp = System.currentTimeMillis()
        )
        repository.saveWeather(entry)
    }

    fun loadHistory() {
        viewModelScope.launch {
            _history.value = repository.getWeatherHistory()
        }
    }

    fun clearError() {
        _error.value = null
    }

}