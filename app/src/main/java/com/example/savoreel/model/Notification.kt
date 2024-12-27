package com.example.savoreel.model

import java.util.Date

data class Notification(
    val notificationId: String = "",
    val recipientId: String = "",
    val type: String = "",
    val senderId: String = "",
    val postId: String = "",
    val date: Date = Date(),
    val description: String = "",
    var isRead: Boolean = false
)