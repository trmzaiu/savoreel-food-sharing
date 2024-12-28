@file:Suppress("UNREACHABLE_CODE")

package com.example.savoreel.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.savoreel.model.Notification
import com.example.savoreel.model.NotificationViewModel
import com.example.savoreel.model.ThemeViewModel
import com.example.savoreel.model.UserViewModel
import com.example.savoreel.model.formatRelativeTime
import com.example.savoreel.ui.component.BackArrow
import com.example.savoreel.ui.profile.UserAvatar
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
    val context = LocalContext.current
    val notificationViewModel: NotificationViewModel = viewModel()
    val userViewModel: UserViewModel = viewModel()
    val currentUser = FirebaseAuth.getInstance().currentUser
    var showMenu by remember { mutableStateOf(false) }

    var notifications by remember { mutableStateOf<List<Notification>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(currentUser) {
        if (currentUser == null) {
            error = "Not logged in"
            loading = false
            return@LaunchedEffect
        }

        try {
            notificationViewModel.getNotifications(
                onSuccess = { notificationsList ->
                    notifications = notificationsList
                    loading = false
                },
                onFailure = { errorMessage ->
                    error = errorMessage
                    loading = false
                }
            )
        } catch (e: Exception) {
            error = e.message ?: "An unexpected error occurred"
            loading = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 20.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(50.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BackArrow()
                    Text(
                        text = "Notifications",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.headlineMedium,
                    )
                }

                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More options",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.secondary)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Mark all as read") },
                            onClick = {
                                notificationViewModel.markAllAsRead(
                                    onSuccess = {
                                        notifications = notifications.map { it.copy(read = true) }
                                    },
                                    onFailure = { /* Handle error */ }
                                )
                                showMenu = false
                            },
                            modifier = Modifier.background(MaterialTheme.colorScheme.secondary)
                        )
                        DropdownMenuItem(
                            text = { Text("Delete all") },
                            onClick = {
                                notificationViewModel.deleteAllNotifications(
                                    onSuccess = {
                                        notifications = emptyList()
                                    },
                                    onFailure = { /* Handle error */ }
                                )
                                showMenu = false
                            },
                            modifier = Modifier.background(MaterialTheme.colorScheme.secondary)
                        )
                    }
                }
            }

            when {
                loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Loading notifications...",
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Error: $error",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                notifications.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No notifications available.",
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        items(
                            count = notifications.size,
                            key = { notifications[it].notificationId }
                        ) { index ->
                            val notification = notifications[index]
                            SwipeableNotificationItem(
                                data = notification,
                                userViewModel = userViewModel,
                                onDelete = {
                                    notificationViewModel.deleteNotification(
                                        notificationId = notification.notificationId,
                                        onSuccess = {
                                            notifications = notifications.filterNot { it.notificationId == notification.notificationId }
                                        },
                                        onFailure = {  }
                                    )
                                },
                                onNotificationClick = { clickedNotification ->
                                    if (!clickedNotification.read) {
                                        notificationViewModel.markAsRead(
                                            notificationId = clickedNotification.notificationId,
                                            onSuccess = { },
                                            onFailure = { }
                                        )
                                    }
                                    val intent = Intent(context, GridPostActivity::class.java).apply {
                                        putExtra("USER_ID", clickedNotification.senderId)
                                    }
                                    context.startActivity(intent)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableNotificationItem(
    data: Notification,
    userViewModel: UserViewModel,
    onDelete: () -> Unit,
    onNotificationClick: (Notification) -> Unit
) {
    var uid by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var imgRes by remember { mutableStateOf("") }
    val isReadBackgroundColor = if (data.read) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.tertiary
    var offset by remember { mutableFloatStateOf(0f) }
    val dismissThreshold = 140f

    LaunchedEffect(data.senderId) {
        userViewModel.getUserById(
            data.senderId,
            onSuccess = { user ->
                if (user != null) {
                    uid = user.userId.toString()
                    name = user.name.toString()
                    imgRes = user.avatarUrl.toString()
                }
            },
            onFailure = { }
        )
    }

    val animatedOffset by animateFloatAsState(targetValue = offset)

    if (animatedOffset < -dismissThreshold) {
        LaunchedEffect(animatedOffset) {
            onDelete()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.CenterEnd
        ) {
            IconButton(
                onClick = { onDelete() },
                modifier = Modifier.scale(1f)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(animatedOffset.toInt(), 0) }
                .background(isReadBackgroundColor)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { _, dragAmount ->
                        offset = (offset + dragAmount).coerceIn(-dismissThreshold, 0f)
                    }
                }
                .clickable { onNotificationClick(data) },
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                UserAvatar(
                    userId = uid,
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
                                fontWeight = MaterialTheme.typography.titleSmall.fontWeight,
                                fontSize = 16.sp
                            )
                        ) {
                            append(name)
                        }
                        withStyle(
                            style = SpanStyle(
                                fontFamily = MaterialTheme.typography.bodyMedium.fontFamily,
                                fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                fontSize = 16.sp
                            )
                        ) {
                            append(" ${data.description}.")
                        }
                        withStyle(
                            style = SpanStyle(
                                fontFamily = MaterialTheme.typography.bodyMedium.fontFamily,
                                fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                color = MaterialTheme.colorScheme.outline,
                                fontSize = 14.sp,
                            )
                        ) {
                            append(" ${formatRelativeTime(data.date)}")
                        }
                    },
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
