package com.example.savoreel.model

data class User(
    val userId: String? = "",
    val name: String? = "",
    val email: String? = "",
    val avatarUrl: String? = "https://sbcf.fr/wp-content/uploads/2018/03/sbcf-default-avatar.png",
    val darkModeEnabled: Boolean = false,
    val following: List<String> = emptyList(),
    val followers: List<String> = emptyList()
)
