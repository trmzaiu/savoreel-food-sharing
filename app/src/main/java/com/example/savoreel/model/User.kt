package com.example.savoreel.model

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class User(val userId: Int, val name: String, val email: String, val password: String)

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val user: User) : LoginState()
    data class Error(val message: String) : LoginState()
}

class UserViewModel : ViewModel() {
    val users = listOf(
        User(1, "Tra My Vu", "tmv@gmail.com", password = "12345678"),
        User(2, "Giang Hoang", email = "gh@gmail.com", password = "87654321"),
        User(3, "Chanh", email = "chanh@gmail.com", password = "password")
    )

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> get() = _loginState

    fun validateUser(email: String, password: String): User? {
        return users.find { it.email == email && it.password == password }
    }

    fun login(email: String, password: String) {
        _loginState.value = LoginState.Loading
        val user = validateUser(email, password)
        _loginState.value = if (user != null) {
            LoginState.Success(user)
        } else {
            LoginState.Error("Make sure you entered your email and password correctly and try again.")
        }
    }

    fun findUserById(userId: Int): User? {
        return users.find { it.userId == userId }
    }
}
