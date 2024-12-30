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
            if (seconds.toInt() == 1) "$seconds second"
            else "$seconds seconds"
        }

        diffInMillis < TimeUnit.HOURS.toMillis(1) -> {
            val minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis)
            if (minutes.toInt() == 1) "$minutes minute"
            else "$minutes minutes"
        }

        diffInMillis < TimeUnit.DAYS.toMillis(1) -> {
            val hours = TimeUnit.MILLISECONDS.toHours(diffInMillis)
            if (hours.toInt() == 1) "$hours hour"
            else "$hours hours"
        }

        diffInMillis < TimeUnit.DAYS.toMillis(7) -> {
            val days = TimeUnit.MILLISECONDS.toDays(diffInMillis)
            if (days.toInt() == 1) "$days day"
            else "$days days"
        }

        else -> {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            dateFormat.format(Date(eventTime))
        }
    }
}