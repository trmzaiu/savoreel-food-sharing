package com.example.savoreel.model

import android.annotation.SuppressLint
import com.example.savoreel.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class Post(
    val postid: Int,
    val userid: Int,
    val title: String,
    val imageRes: Int,
    val datetime: Date,
)

@SuppressLint("NewApi")
val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
@SuppressLint("NewApi")
val postss = List(200) { i ->
    val calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, 2023)
        set(Calendar.MONTH, i % 12)
        set(Calendar.DAY_OF_MONTH, i % 28 + 1)
        set(Calendar.HOUR_OF_DAY, 10)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }
    Post(
        postid = i + 1,
        userid = i + 1,
        title = "Post ${i + 1}",
        imageRes = R.drawable.food,
        datetime = calendar.time // Sử dụng `Date` thay vì `LocalDateTime`
    )
}

val posts = groupPostsByMonth(postss)

@SuppressLint("NewApi")
fun groupPostsByMonth(posts: List<Post>): Map<Pair<Int, String>, List<Post>> {
    return posts.groupBy { post ->
        val calendar = Calendar.getInstance()
        calendar.time = post.datetime
        Pair(calendar.get(Calendar.YEAR), getMonthName(calendar.get(Calendar.MONTH) + 1))
    }
}


fun getMonthName(month: Int): String {
    val dateFormat = SimpleDateFormat("MMMM", Locale.ENGLISH) // "MMMM" trả về tên đầy đủ của tháng
    val calendar = Calendar.getInstance().apply {
        set(Calendar.MONTH, month - 1) // Calendar.MONTH bắt đầu từ 0
    }
    return dateFormat.format(calendar.time)
}