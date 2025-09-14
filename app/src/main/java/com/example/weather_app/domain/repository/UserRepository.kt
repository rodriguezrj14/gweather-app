package com.example.weather_app.domain.repository

import com.example.weather_app.domain.model.User

interface UserRepository {
    fun registerUser(user: User): Boolean
    fun login(username: String, password: String): User?
}