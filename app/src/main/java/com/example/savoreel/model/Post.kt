package com.example.savoreel.model

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.savoreel.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class Post(
    val postId: String,
    val userId: String,
    val title: String,
    val imageRes: String,
    val datetime: Date,
)

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
        postId = "${i + 1}",
        userId = "${i + 1}",
        title = "Post ${i + 1}",
        imageRes = "https://sbcf.fr/wp-content/uploads/2018/03/sbcf-default-avatar.png",
        datetime = calendar.time
    )
}

val posts = groupPostsByMonth(postss)

@SuppressLint("NewApi")
fun groupPostsByMonth(posts: List<Post>): Map<Pair<Int, Int>, List<Post>> {
    val comparator = compareByDescending<Pair<Int, Int>> { it.first }
        .thenByDescending { it.second }

    return posts.groupBy { post ->
        val calendar = Calendar.getInstance()
        calendar.time = post.datetime
        Pair(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1)
    }.mapValues { entry ->
        entry.value.sortedByDescending { it.datetime }
    }.toSortedMap(comparator)
}

fun getMonthName(month: Int): String {
    val dateFormat = SimpleDateFormat("MMMM", Locale.ENGLISH)
    val calendar = Calendar.getInstance().apply {
        set(Calendar.MONTH, month - 1)
    }
    return dateFormat.format(calendar.time)
}


@Composable
fun PostImage(
    url: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(url)
            .crossfade(true)
            .build()
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        when (painter.state) {
            is AsyncImagePainter.State.Loading -> {
                CircularProgressIndicator()
            }
            is AsyncImagePainter.State.Error -> {
                Image(
                    painter = painterResource(R.drawable.avatar_error),
                    contentDescription = "Error loading post",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            else -> {
                Image(
                    painter = painter,
                    contentDescription = "Post image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}