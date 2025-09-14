package com.example.weather_app.data.repository

import android.content.ContentValues
import com.example.weather_app.data.local.DatabaseHelper
import com.example.weather_app.data.local.UserTable
import com.example.weather_app.domain.model.User
import com.example.weather_app.domain.repository.UserRepository
import java.security.MessageDigest
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(private val dbHelper: DatabaseHelper) : UserRepository {

    override fun registerUser(user: User): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(UserTable.COLUMN_USERNAME, user.username)
            put(UserTable.COLUMN_PASSWORD, hashPassword(user.password))
        }

        val result = db.insert(UserTable.TABLE_NAME, null, values)
        db.close()
        return result != -1L
    }

    override fun login(
        username: String,
        password: String
    ): User? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            UserTable.TABLE_NAME,
            arrayOf(UserTable.COLUMN_ID, UserTable.COLUMN_USERNAME, UserTable.COLUMN_PASSWORD),
            "${UserTable.COLUMN_USERNAME}=? AND ${UserTable.COLUMN_PASSWORD}=?",
            arrayOf(username, hashPassword(password)),
            null, null, null
        )

        val user = if (cursor.moveToFirst()) {
            User(
                id = cursor.getInt(0),
                username = cursor.getString(1),
                password = cursor.getString(2)
            )
        } else null

        cursor.close()
        db.close()
        return user
    }

    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }
}