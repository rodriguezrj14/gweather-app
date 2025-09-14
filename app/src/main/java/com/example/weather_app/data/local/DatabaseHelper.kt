package com.example.weather_app.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "weather_app.db", null, 1) {

    override fun onCreate(database: SQLiteDatabase) {
        database.execSQL(UserTable.CREATE_TABLE)
        database.execSQL(WeatherHistoryTable.CREATE_TABLE)
    }

    override fun onUpgrade(
        database: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        database.execSQL("DROP TABLE IF EXISTS ${UserTable.TABLE_NAME}")
        database.execSQL("DROP TABLE IF EXISTS ${WeatherHistoryTable.TABLE_NAME}")
        onCreate(database)
    }
}