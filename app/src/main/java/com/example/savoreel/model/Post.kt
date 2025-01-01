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
    val date: Timestamp = getDateReducedByMonthsAndDays(),
    val photoUri: String = "",
    val reactions: Map<String, Int> = emptyMap(),
)

fun getDateReducedByMonthsAndDays(): Timestamp {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.MONTH, -3)
    calendar.add(Calendar.DAY_OF_MONTH, -3)
    return Timestamp(calendar.time)
}


