package com.example.weather_app

import com.example.weather_app.data.remote.WeatherApi
import com.example.weather_app.domain.model.Main
import com.example.weather_app.domain.model.Sys
import com.example.weather_app.domain.model.Weather
import com.example.weather_app.domain.model.WeatherResponse

class FakeWeatherApi : WeatherApi {
    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        apiKey: String,
        units: String
    ): WeatherResponse {
        return WeatherResponse(
            weather = listOf(Weather(804, "Clouds", "overcast clouds", "04d")),
            main = Main(temp = 300.09f),
            sys = Sys(country = "PH", sunrise = 1757799859, sunset = 1757843868),
            name = "Calapan"
        )
    }
}