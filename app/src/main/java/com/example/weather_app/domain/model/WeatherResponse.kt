package com.example.weather_app.domain.model

data class WeatherResponse(
    val weather: List<Weather>,
    val main: Main,
    val sys: Sys,
    val name: String
)
