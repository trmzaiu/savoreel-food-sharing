package com.example.savoreel.model

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

data class Notification(
    val notificationId: String = "",
    val recipientId: String = "",
    val senderId: String = "",
    val postId: String = "",
    val date: Timestamp = Timestamp.now(),
    val description: String = "",
    var read: Boolean = false,
    val type: String = ""
)

fun formatRelativeTime(timestamp: Timestamp): String {
    val currentTime = System.currentTimeMillis()
    val eventTime = timestamp.toDate().time
    val diffInMillis = currentTime - eventTime

    return when {
        diffInMillis < TimeUnit.MINUTES.toMillis(1) -> {
            val seconds = TimeUnit.MILLISECONDS.toSeconds(diffInMillis)
            "${seconds}s"
        }

        diffInMillis < TimeUnit.HOURS.toMillis(1) -> {
            val minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis)
            "${minutes}m"
        }

        diffInMillis < TimeUnit.DAYS.toMillis(1) -> {
            val hours = TimeUnit.MILLISECONDS.toHours(diffInMillis)
            "${hours}h"
        }

        diffInMillis < TimeUnit.DAYS.toMillis(7) -> {
            val days = TimeUnit.MILLISECONDS.toDays(diffInMillis)
            "${days}d"
        }

        else -> {
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            dateFormat.format(Date(eventTime))
        }
    }
}