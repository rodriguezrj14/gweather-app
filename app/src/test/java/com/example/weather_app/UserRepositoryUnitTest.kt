package com.example.weather_app

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.weather_app.data.local.DatabaseHelper
import com.example.weather_app.data.repository.UserRepositoryImpl
import com.example.weather_app.domain.model.User
import com.example.weather_app.domain.repository.UserRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.any
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.anyOrNull
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class UserRepositoryUnitTest {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var writableDb: SQLiteDatabase
    private lateinit var readableDb: SQLiteDatabase
    private lateinit var cursor: Cursor
    private lateinit var userRepository: UserRepository

    @Before
    fun setup() {

        dbHelper = mock(DatabaseHelper::class.java)
        writableDb = mock(SQLiteDatabase::class.java)
        readableDb = mock(SQLiteDatabase::class.java)
        cursor = mock(Cursor::class.java)

        `when`(dbHelper.writableDatabase).thenReturn(writableDb)
        `when`(dbHelper.readableDatabase).thenReturn(readableDb)

        userRepository = UserRepositoryImpl(dbHelper)
    }

    @Test
    fun `registerUser inserts user successfully`() {
        val user = User(username = "test", password = "123")
        `when`(
            writableDb.insert(
                anyString(),
                anyOrNull(),
                any(ContentValues::class.java)
            )
        ).thenReturn(1L)

        val result = userRepository.registerUser(user)
        assertTrue(result)

        verify(writableDb).insert(anyString(), anyOrNull(), any(ContentValues::class.java))
        verify(writableDb).close()
    }

    @Test
    fun `login returns user if exists`() {
        val user = User(id = 1, username = "test", password = "123")

        `when`(
            readableDb.query(
                anyString(),
                any(),
                anyString(),
                any(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenReturn(cursor)

        `when`(cursor.moveToFirst()).thenReturn(true)
        `when`(cursor.getInt(0)).thenReturn(user.id)
        `when`(cursor.getString(1)).thenReturn(user.username)
        `when`(cursor.getString(2)).thenReturn(user.password)

        val loggedInUser = userRepository.login("test", "123")
        assertNotNull(loggedInUser)
        assertEquals(user.username, loggedInUser?.username)

        verify(cursor).close()
        verify(readableDb).close()
    }

    @Test
    fun `login returns null for wrong credentials`() {
        `when`(
            readableDb.query(
                anyString(),
                any(),
                anyString(),
                any(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenReturn(cursor)

        `when`(cursor.moveToFirst()).thenReturn(false)

        val result = userRepository.login("wrong", "pass")
        assertNull(result)

        verify(cursor).close()
        verify(readableDb).close()
    }

}