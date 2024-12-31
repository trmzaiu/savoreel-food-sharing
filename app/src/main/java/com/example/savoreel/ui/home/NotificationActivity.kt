@file:Suppress("UNREACHABLE_CODE")

package com.example.savoreel.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.example.savoreel.ui.profile.UserWithOutAvatar
import com.example.savoreel.ui.theme.SavoreelTheme

class NotificationActivity: ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()
    private val notificationViewModel: NotificationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        themeViewModel.loadUserSettings()

        setContent {
            val isDarkMode by themeViewModel.isDarkModeEnabled.collectAsState()

            SavoreelTheme(darkTheme = isDarkMode) {
                val notifications by notificationViewModel.notifications.collectAsState()
                val unreadCount by notificationViewModel.unreadCount.collectAsState()
                NotificationScreen(notifications, unreadCount)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(notifications: List<Notification>, unreadCount: Int) {
    val context = LocalContext.current
    val notificationViewModel: NotificationViewModel = viewModel()
    var showMenu by remember { mutableStateOf(false) }

    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            Log.d("NotificationScreen", "Start observing notifications...")
            notificationViewModel.startObservingNotifications(
                onError = { errorMessage ->
                    error = errorMessage
                    loading = false
                    Log.e("NotificationScreen", "Error observing notifications: $errorMessage")
                }
            )
            loading = false
        } catch (e: Exception) {
            error = e.message ?: "An unexpected error occurred"
            loading = false
            Log.e("NotificationScreen", "Error observing notifications: $error")
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            Log.d("NotificationScreen", "Stopping observation of notifications")
            notificationViewModel.stopObservingNotifications()
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
                    IconButton(
                        modifier = Modifier.size(48.dp).clip(RoundedCornerShape(0)),
                        onClick = { showMenu = true }
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                modifier = Modifier.size(30.dp),
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More options",
                                tint = MaterialTheme.colorScheme.onBackground
                            )

                            if (unreadCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .background(MaterialTheme.colorScheme.error, CircleShape)
                                        .align(Alignment.TopEnd),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = unreadCount.toString(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onError,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }

                    if (showMenu){
                        ModalBottomSheet(
                            onDismissRequest = { showMenu = false },
                            sheetState = rememberModalBottomSheetState(
                                skipPartiallyExpanded = true
                            ),
                            containerColor = MaterialTheme.colorScheme.secondary.copy(0.95f),
                            dragHandle = { },
                            windowInsets = WindowInsets(0,0,0,0),
                            content = {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text ="Mark all as read",
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth(),
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    },
                                    onClick = {
                                        Log.d("NotificationScreen", "Marking all notifications as read")
                                        notificationViewModel.markAllAsRead(
                                            onSuccess = {
                                                Log.d("NotificationScreen", "All notifications marked as read")
                                            },
                                            onFailure = { error ->
                                                Log.e("NotificationScreen", "Failed to mark all as read: $error")
                                            }
                                        )
                                        showMenu = false
                                    },
                                    modifier = Modifier.background(MaterialTheme.colorScheme.secondary).fillMaxWidth()
                                )
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text ="Delete All",
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth(),
                                            color = MaterialTheme.colorScheme.primary,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                   },
                                    onClick = {
                                        Log.d("NotificationScreen", "Deleting all notifications")
                                        notificationViewModel.deleteAllNotifications(
                                            onSuccess = {
                                                Log.d("NotificationScreen", "All notifications deleted")
                                            },
                                            onFailure = { error ->
                                                Log.e("NotificationScreen", "Failed to delete all notifications: $error")
                                            }
                                        )
                                        showMenu = false
                                    },
                                    modifier = Modifier.background(MaterialTheme.colorScheme.secondary).fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                            }
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
                            NotificationItem(
                                data = notification,
                                onDelete = {
                                    Log.d("NotificationScreen", "Deleting notification with ID: ${notification.notificationId}")
                                    notificationViewModel.deleteNotification(
                                        notificationId = notification.notificationId,
                                        onSuccess = {
                                            Log.d("NotificationScreen", "Notification deleted successfully")
                                        },
                                        onFailure = { error ->
                                            Log.e("NotificationScreen", "Failed to delete notification: $error")
                                        }
                                    )
                                },
                                onNotificationClick = { clickedNotification ->
                                    if (!clickedNotification.read) {
                                        Log.d("NotificationScreen", "Marking notification as read with ID: ${clickedNotification.notificationId}")
                                        notificationViewModel.markAsRead(
                                            notificationId = clickedNotification.notificationId,
                                            onSuccess = {
                                                Log.d("NotificationScreen", "Notification marked as read")
                                            },
                                            onFailure = { error ->
                                                Log.e("NotificationScreen", "Failed to mark notification as read: $error")
                                            }
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

@Composable
fun NotificationItem(
    data: Notification,
    onDelete: () -> Unit,
    onNotificationClick: (Notification) -> Unit
) {
    val userViewModel: UserViewModel = viewModel()
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
                if (imgRes.isNotEmpty()) {
                    UserAvatar(imgRes, 40.dp)
                } else {
                    UserWithOutAvatar(name, 20.sp, 40.dp)
                }

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
                            append(" ${data.description}")
                        }
                        withStyle(
                            style = SpanStyle(
                                fontFamily = MaterialTheme.typography.bodyMedium.fontFamily,
                                fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                                color = MaterialTheme.colorScheme.outline,
                                fontSize = 14.sp,
                            )
                        ) {
                            append("\n${formatRelativeTime(data.date)}")
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