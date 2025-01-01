//package com.example.savoreel.model
//
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import systemSendNotification
//
//class NotificationReceiver : BroadcastReceiver() {
//    override fun onReceive(context: Context, intent: Intent) {
//        val title = intent.getStringExtra("TITLE") ?: "Thông báo mới"
//        val message = intent.getStringExtra("MESSAGE") ?: "Có thông báo mới"
//
//        // Gửi thông báo hệ thống
//        systemSendNotification(context, title, message)
//    }
//}
