package com.example.weather_app.domain.model

data class WeatherEntry (
    val city: String,
    val country: String,
    val temp: Float,
    val description: String,
    val icon: String,
    val timestamp: Long
)