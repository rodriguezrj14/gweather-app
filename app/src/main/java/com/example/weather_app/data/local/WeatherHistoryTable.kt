package com.example.weather_app.data.local

object WeatherHistoryTable {

    const val TABLE_NAME = "weather_history"

    const val COLUMN_ID = "id"
    const val COLUMN_CITY = "city"
    const val COLUMN_COUNTRY = "country"
    const val COLUMN_TEMP = "temp"
    const val COLUMN_DESCRIPTION = "description"
    const val COLUMN_ICON = "icon"
    const val COLUMN_TIMESTAMP = "timestamp"

    const val CREATE_TABLE = """
        CREATE TABLE $TABLE_NAME (
            $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_CITY TEXT NOT NULL,
            $COLUMN_COUNTRY TEXT NOT NULL,
            $COLUMN_TEMP REAL NOT NULL,
            $COLUMN_DESCRIPTION TEXT,
            $COLUMN_ICON TEXT,
            $COLUMN_TIMESTAMP LONG NOT NULL
        )
    """
}