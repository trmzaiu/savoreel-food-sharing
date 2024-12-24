package com.example.savoreel.model

import java.util.Date

data class Notification(
    val userid: String,
    val postid: String,
    val action: String,
    val date: Date,
){

}