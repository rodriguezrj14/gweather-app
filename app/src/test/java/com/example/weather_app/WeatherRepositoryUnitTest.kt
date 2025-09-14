package com.example.weather_app

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.weather_app.data.local.DatabaseHelper
import com.example.weather_app.data.local.WeatherHistoryTable
import com.example.weather_app.data.repository.WeatherRepositoryImpl
import com.example.weather_app.domain.model.WeatherEntry
import com.example.weather_app.domain.repository.WeatherRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.any
import org.mockito.Mockito.anyString
import org.mockito.Mockito.eq
import org.mockito.Mockito.isNull
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.anyOrNull
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class WeatherRepositoryUnitTest {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var writableDb: SQLiteDatabase
    private lateinit var readableDb: SQLiteDatabase
    private lateinit var cursor: Cursor
    private lateinit var weatherRepository: WeatherRepository

    @Before
    fun setup() {
        dbHelper = mock(DatabaseHelper::class.java)
        writableDb = mock(SQLiteDatabase::class.java)
        readableDb = mock(SQLiteDatabase::class.java)
        cursor = mock(Cursor::class.java)

        `when`(dbHelper.writableDatabase).thenReturn(writableDb)
        `when`(dbHelper.readableDatabase).thenReturn(readableDb)

        weatherRepository = WeatherRepositoryImpl(
            api = FakeWeatherApi(),
            dbHelper = dbHelper
        )
    }

    @Test
    fun `saveWeather inserts weather entry`() {
        val entry = WeatherEntry(
            city = "Calapan",
            country = "PH",
            temp = 300.09f,
            description = "overcast clouds",
            icon = "04d",
            timestamp = System.currentTimeMillis()
        )

        `when`(writableDb.insert(anyString(), anyOrNull(), any(ContentValues::class.java)))
            .thenReturn(1L)

        weatherRepository.saveWeather(entry)

        verify(writableDb).insert(
            eq(WeatherHistoryTable.TABLE_NAME),
            isNull(),
            any(ContentValues::class.java)
        )
        verify(writableDb).close()
    }

    @Test
    fun `getWeatherHistory returns saved entries`() {
        val entry = WeatherEntry(
            city = "Calapan",
            country = "PH",
            temp = 300.09f,
            description = "overcast clouds",
            icon = "04d",
            timestamp = System.currentTimeMillis()
        )

        // mock cursor
        `when`(readableDb.rawQuery(anyString(), any())).thenReturn(cursor)
        `when`(cursor.moveToFirst()).thenReturn(true)
        `when`(cursor.moveToNext()).thenReturn(false)
        `when`(cursor.getColumnIndexOrThrow(anyString())).thenAnswer { invocation ->
            when (invocation.arguments[0] as String) {
                WeatherHistoryTable.COLUMN_ID -> 0
                WeatherHistoryTable.COLUMN_CITY -> 1
                WeatherHistoryTable.COLUMN_COUNTRY -> 2
                WeatherHistoryTable.COLUMN_TEMP -> 3
                WeatherHistoryTable.COLUMN_DESCRIPTION -> 4
                WeatherHistoryTable.COLUMN_ICON -> 5
                WeatherHistoryTable.COLUMN_TIMESTAMP -> 6
                else -> -1
            }
        }
        `when`(cursor.getString(anyInt())).thenAnswer { invocation ->
            when (invocation.arguments[0] as Int) {
                1 -> entry.city
                2 -> entry.country
                4 -> entry.description
                5 -> entry.icon
                else -> null
            }
        }
        `when`(cursor.getDouble(anyInt())).thenAnswer { invocation ->
            val index = invocation.arguments[0] as Int
            if (index == 3) entry.temp.toDouble() else 0.0
        }
        `when`(cursor.getLong(anyInt())).thenAnswer { invocation ->
            val index = invocation.arguments[0] as Int
            if (index == 6) entry.timestamp else 0L
        }

        val history = weatherRepository.getWeatherHistory()

        assertEquals(1, history.size)
        val savedEntry = history[0]
        assertEquals(entry.city, savedEntry.city)
        assertEquals(entry.country, savedEntry.country)
        assertEquals(entry.temp, savedEntry.temp)
        assertEquals(entry.description, savedEntry.description)
        assertEquals(entry.icon, savedEntry.icon)

        verify(cursor).close()
        verify(readableDb).close()
    }


    @Test
    fun `fetchCurrentWeather returns sample response`() = runTest {
        val result = weatherRepository.fetchCurrentWeather(13.3997, 121.1878)
        assertEquals("Calapan", result.name)
        assertEquals("PH", result.sys.country)
        assertEquals(300.09f, result.main.temp)
        assertEquals("overcast clouds", result.weather.first().description)
        assertEquals("04d", result.weather.first().icon)
    }
}
