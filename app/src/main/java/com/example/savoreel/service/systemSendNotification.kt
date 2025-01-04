package com.example.savoreel

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.savoreel.model.Notification

@SuppressLint("MissingPermission")
fun sendSystemNotification(name: String, notification: Notification) {
    val context = MyApp.appContext
    val channelId = "notification_channel"
    val channelName = "Notification Channel"
    val channelDescription = "Savoreel Notifications"

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = channelDescription
            
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.rounded_logo)
        .setContentTitle("Savoreel")
        .setContentText("Chung Anh ${notification.description}")
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    with(NotificationManagerCompat.from(context)) {
        notify(notification.notificationId.hashCode(), builder.build())
    }
}