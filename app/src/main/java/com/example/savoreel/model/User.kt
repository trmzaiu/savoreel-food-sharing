package com.example.savoreel.model

data class User(
    val userId: String = "",
    val name: String? = "",
    val email: String? = "",
    val avatarUri: String? = "",
    val darkModeEnabled: Boolean = false,
    val following: List<String> = emptyList(),
    val followers: List<String> = emptyList()
)


