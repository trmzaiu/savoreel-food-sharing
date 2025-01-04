package com.example.savoreel.model

import com.google.firebase.Timestamp
import java.util.Calendar
import kotlin.random.Random

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
    val randomDays = Random.nextInt(1, 30)
    calendar.add(Calendar.MONTH, 0)
    calendar.add(Calendar.DAY_OF_MONTH, 0)
    return Timestamp(calendar.time)
}


