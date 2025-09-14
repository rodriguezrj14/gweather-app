package com.example.weather_app.data.local

object UserTable {
    const val TABLE_NAME = "users"
    const val COLUMN_ID = "id"
    const val COLUMN_USERNAME = "username"
    const val COLUMN_PASSWORD = "password"

    const val CREATE_TABLE = """
        CREATE TABLE $TABLE_NAME (
            $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_USERNAME TEXT UNIQUE,
            $COLUMN_PASSWORD TEXT
        )
    """
}