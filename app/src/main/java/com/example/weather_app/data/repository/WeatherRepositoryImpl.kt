package com.example.weather_app.data.repository

import android.content.ContentValues
import com.example.weather_app.BuildConfig
import com.example.weather_app.data.local.DatabaseHelper
import com.example.weather_app.data.local.WeatherHistoryTable
import com.example.weather_app.data.remote.WeatherApi
import com.example.weather_app.domain.model.WeatherEntry
import com.example.weather_app.domain.model.WeatherResponse
import com.example.weather_app.domain.repository.WeatherRepository
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApi,
    private val dbHelper: DatabaseHelper
) : WeatherRepository {

    private val apiKey = BuildConfig.OPENWEATHER_API_KEY

    override suspend fun fetchCurrentWeather(lat: Double, lon: Double): WeatherResponse {
        return api.getCurrentWeather(lat, lon, apiKey)
    }

    override fun saveWeather(weatherEntry: WeatherEntry) {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(WeatherHistoryTable.COLUMN_CITY, weatherEntry.city)
            put(WeatherHistoryTable.COLUMN_COUNTRY, weatherEntry.country)
            put(WeatherHistoryTable.COLUMN_TEMP, weatherEntry.temp)
            put(WeatherHistoryTable.COLUMN_DESCRIPTION, weatherEntry.description)
            put(WeatherHistoryTable.COLUMN_ICON, weatherEntry.icon)
            put(WeatherHistoryTable.COLUMN_TIMESTAMP, weatherEntry.timestamp)
        }

        db.insert(WeatherHistoryTable.TABLE_NAME, null, values)
        db.close()
    }

    override fun getWeatherHistory(): List<WeatherEntry> {
        val list = mutableListOf<WeatherEntry>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM ${WeatherHistoryTable.TABLE_NAME} ORDER BY ${WeatherHistoryTable.COLUMN_TIMESTAMP} DESC",
            null
        )
        if (cursor.moveToFirst()) {
            do {
                val entry = WeatherEntry(
                    city = cursor.getString(cursor.getColumnIndexOrThrow(WeatherHistoryTable.COLUMN_CITY)),
                    country = cursor.getString(cursor.getColumnIndexOrThrow(WeatherHistoryTable.COLUMN_COUNTRY)),
                    temp = cursor.getDouble(cursor.getColumnIndexOrThrow(WeatherHistoryTable.COLUMN_TEMP)).toFloat(),
                    description = cursor.getString(cursor.getColumnIndexOrThrow(WeatherHistoryTable.COLUMN_DESCRIPTION)),
                    icon = cursor.getString(cursor.getColumnIndexOrThrow(WeatherHistoryTable.COLUMN_ICON)),
                    timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(WeatherHistoryTable.COLUMN_TIMESTAMP))
                )
                list.add(entry)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return list
    }
}