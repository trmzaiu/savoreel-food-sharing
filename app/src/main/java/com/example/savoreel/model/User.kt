package com.example.savoreel.model

data class User(
    val userId: String? = "",
    val name: String? = "",
    val email: String? = "",
    val avatarUrl: String? = "",
    val darkModeEnabled: Boolean = false,
    val following: List<String> = emptyList(),
    val followers: List<String> = emptyList(),
    val nameKeywords: List<String> = emptyList(),
    val hashtags: List<String> = emptyList(),
    val searchHistory: List<SearchHistoryItem> = emptyList()
)

data class SearchHistoryItem(
    val keyword: String? = "",
    var timestamp: Long? = 0L
)