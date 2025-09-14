package com.example.weather_app.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weather_app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message
    private val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean> get() = _success

    fun login(username: String, password: String) {
        if (username.isEmpty() || password.isEmpty()) {
            _message.value = "Please fill in all fields"
            return
        }

        val user = userRepository.login(username, password)

        if (user != null) {
            _message.value = "Login successful for $username"
            _success.value = true
        } else {
            _message.value = "Login failed: Invalid username or password"
        }
    }
}