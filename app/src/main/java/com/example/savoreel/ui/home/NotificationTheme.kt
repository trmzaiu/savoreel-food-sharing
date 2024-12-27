@file:Suppress("UNREACHABLE_CODE")

package com.example.savoreel.ui.home

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.savoreel.R
import com.example.savoreel.model.Notification
import com.example.savoreel.model.NotificationViewModel
import com.example.savoreel.model.UserViewModel
import com.example.savoreel.ui.component.BackArrow
import com.example.savoreel.ui.theme.SavoreelTheme
import com.google.firebase.auth.FirebaseAuth
import java.io.InputStream
import java.net.URL

@Composable
fun NotificationTheme(navController: NavController, notificationViewModel: NotificationViewModel, userViewModel: UserViewModel) {
    val currentUser = FirebaseAuth.getInstance().currentUser

    val (notifications, setNotifications) = remember { mutableStateOf<List<Notification>>(emptyList()) }
    val (error, setError) = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(currentUser) {
        currentUser?.uid?.let { recipientId ->
            notificationViewModel.getNotificationsForRecipient(
                recipientId = recipientId,
                onSuccess = { setNotifications(it) },
                onFail = { setError(it) }
            )
        }
    }

    Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.padding(top = 40.dp).padding(horizontal = 20.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(50.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                BackArrow()
                Text(
                    text = "Notifications",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.headlineMedium,
                )
            }

            ImageFromUrl(
                url = "https://sbcf.fr/wp-content/uploads/2018/03/sbcf-default-avatar.png",
            )

            // Xử lý trạng thái lỗi
            error?.let {
                Text(
                    text = "Error: $it",
                    color = MaterialTheme.colorScheme.error
                )
                return@Column
            }

            if (notifications.isEmpty()) {
                Text(
                    text = "No notifications available.",
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                LazyColumn {
                    items(count = notifications.size) { index ->
                        val notification = notifications[index]
                        NotificationItem(data = notification, userViewModel)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationScreenPreview() {
    SavoreelTheme(darkTheme = false) {
        NotificationTheme(
            navController = rememberNavController(),
            notificationViewModel = NotificationViewModel(),
            userViewModel = UserViewModel()
        )
    }
}

@Composable
fun ImageFromUrl(url: String, modifier: Modifier = Modifier) {
    val imageBitmap: ImageBitmap? = loadImageBitmapFromUrl(url)

    if (imageBitmap != null) {
        Image(
            bitmap = imageBitmap,
            contentDescription = null,
            modifier = modifier.fillMaxSize()
        )
    } else {
        // Handle fallback if necessary
        Image(
            painter = painterResource(id = R.drawable.default_avatar),
            contentDescription = null,
            modifier = modifier.fillMaxSize()
        )
    }
}

private fun loadImageBitmapFromUrl(url: String): ImageBitmap? {
    return try {
        val inputStream: InputStream = URL(url).openStream()
        val bitmap = BitmapFactory.decodeStream(inputStream)
        bitmap.asImageBitmap()
    } catch (e: Exception) {
        null
    }
}