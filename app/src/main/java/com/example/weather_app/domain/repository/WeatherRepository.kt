package com.example.weather_app.domain.repository

import com.example.weather_app.domain.model.WeatherEntry
import com.example.weather_app.domain.model.WeatherResponse

interface WeatherRepository {
    suspend fun fetchCurrentWeather(lat: Double, lon: Double): WeatherResponse
    fun saveWeather(weatherEntry: WeatherEntry)
    fun getWeatherHistory(): List<WeatherEntry>
}
