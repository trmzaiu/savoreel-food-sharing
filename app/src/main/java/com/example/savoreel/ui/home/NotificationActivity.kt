@file:Suppress("UNREACHABLE_CODE")

package com.example.savoreel.ui.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.savoreel.model.Notification
import com.example.savoreel.model.NotificationViewModel
import com.example.savoreel.model.ThemeViewModel
import com.example.savoreel.model.UserViewModel
import com.example.savoreel.ui.component.BackArrow
import com.example.savoreel.ui.theme.SavoreelTheme
import com.google.firebase.auth.FirebaseAuth

class NotificationActivity: ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        themeViewModel.loadUserSettings()

        setContent {
            val isDarkMode by themeViewModel.isDarkModeEnabled.observeAsState(initial = false)

            SavoreelTheme(darkTheme = isDarkMode) {
                NotificationScreen()
            }
        }
    }
}

@Composable
fun NotificationScreen() {
    val notificationViewModel: NotificationViewModel = viewModel()
    val userViewModel: UserViewModel = viewModel()
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

@Composable
fun NotificationItem(data: Notification, userViewModel: UserViewModel) {
    var name by remember { mutableStateOf("") }
    var imgRes by remember { mutableStateOf("") }
    val user = userViewModel.getUserById(
        data.recipientId,
        onSuccess = {user ->
            if (user!=null) {
                name = user.name.toString()
                imgRes = user.avatarUri.toString()
            }
        },
        onFailure = TODO(),
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ImageFromUrl(
            url = imgRes,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        fontFamily = MaterialTheme.typography.titleSmall.fontFamily,
                        fontWeight = MaterialTheme.typography.titleSmall.fontWeight
                    )
                ) {
                    append(name) // Replace with actual user name lookup
                }
                withStyle(
                    style = SpanStyle(
                        fontFamily = MaterialTheme.typography.bodyMedium.fontFamily,
                        fontWeight = MaterialTheme.typography.bodyMedium.fontWeight
                    )
                ) {
                    append(" ${data.description}")
                }

                withStyle(
                    style = SpanStyle(
                        fontFamily = MaterialTheme.typography.bodyMedium.fontFamily,
                        fontWeight = MaterialTheme.typography.bodyMedium.fontWeight
                    )
                ) {
                    append(" ${data.date}")
                }
            },
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

    }
}