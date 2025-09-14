package com.example.weather_app.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weather_app.domain.model.User
import com.example.weather_app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message
    private val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean> get() = _success

    fun register(username: String, password: String, confirmPassword: String) {
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            _message.value = "Please fill in all fields"
            return
        }

        if (password != confirmPassword) {
            _message.value = "Passwords do not match"
            return
        }

        val user = User(username = username, password = password)
        val success = userRepository.registerUser(user)

        if (success) {
            _message.value = "Registration successful for $username"
            _success.value = true
        } else {
            _message.value = "Registration failed: Username already exist"
        }
    }
}