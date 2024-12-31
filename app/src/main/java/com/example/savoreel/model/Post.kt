package com.example.savoreel.model

import com.google.firebase.Timestamp

data class Post(
    val postId: String = "",
    val userId: String = "",
    val name: String = "",
    val title: String = "",
    val hashtag: List<String>? = emptyList(),
    val location: String = "",
    val date: Timestamp = Timestamp.now(),
    val photoUri: String = "",
    val reactions: Map<String, Int> = emptyMap(),
)