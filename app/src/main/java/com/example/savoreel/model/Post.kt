package com.example.savoreel.model

import com.google.firebase.Timestamp
import java.util.Calendar

data class Post(
    val postId: String = "",
    val userId: String = "",
    val name: String = "",
    val title: String = "",
    val hashtag: List<String>? = emptyList(),
    val location: String = "",
    val date: Timestamp = getDateOneMonthLater(),
    val photoUri: String = "",
    val reactions: Map<String, Int> = emptyMap(),
)

fun getDateOneMonthLater(): Timestamp {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.MONTH, -1)
    return Timestamp(calendar.time)
}