package com.example.savoreel.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.savoreel.model.ThemeViewModel
import com.example.savoreel.model.UserViewModel
import com.example.savoreel.ui.component.BackArrow
import com.example.savoreel.ui.home.GridPostActivity
import com.example.savoreel.ui.theme.SavoreelTheme
import com.example.savoreel.ui.theme.nunitoFontFamily

class FollowActivity: ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        themeViewModel.loadUserSettings()

        val initialTab = intent.getStringExtra("TAB") ?: "following"
        val userId = intent.getStringExtra("USER_ID")

        setContent {
            val isDarkMode by themeViewModel.isDarkModeEnabled.collectAsState()

            SavoreelTheme(darkTheme = isDarkMode) {
                FollowScreen(initialTab = initialTab, userId = userId) { userId ->
                    val intent = Intent(this, GridPostActivity::class.java).apply {
                        putExtra("USER_ID", userId)
                    }
                    startActivity(intent)
                }

            }
        }
    }

    override fun onResume() {
        super.onResume()
        themeViewModel.loadUserSettings()
        userViewModel.getUser(
            onSuccess = { user -> userViewModel.setUser(user) },
            onFailure = { error -> Log.e("SettingActivity", "Error retrieving user: $error") }
        )
    }
}

@Composable
fun FollowScreen(initialTab: String, userId: String?, onUserClick: (String) -> Unit)  {
    val userViewModel: UserViewModel = viewModel()

    var selectedTab by remember { mutableIntStateOf(if (initialTab == "following") 0 else 1) }
    var followingList by remember { mutableStateOf<List<String>>(emptyList()) }
    var followerList by remember { mutableStateOf<List<String>>(emptyList()) }
    var name by remember { mutableStateOf("") }

    val tabs = listOf("Following", "Followers")

    // Track whether data has been loaded
    var isDataLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        if (userId != null) {
            userViewModel.getUserById(userId,
                onSuccess = { user ->
                    name = user?.name.toString()
                },
                onFailure = { error -> Log.e("UserList", "Error: $error") }
            )
        }
    }

    // When tab changes, load the corresponding data
    LaunchedEffect(selectedTab) {
        if (selectedTab == 0 && userId != null) {
            userViewModel.getFollowing(userId,
                onSuccess = { followingIds ->
                    followingList = followingIds.toMutableList()
                    isDataLoaded = true
                    Log.d("FollowScreen", "Following list: $followingIds, $followingList")
                },
                onFailure = { error ->
                    Log.e("FollowScreen", "Error fetching following: $error")
                    isDataLoaded = true
                }
            )
        } else if (selectedTab == 1 && userId != null) {
            userViewModel.getFollowers(userId,
                onSuccess = { followersIds ->
                    followerList = followersIds.toMutableList()
                    isDataLoaded = true
                    Log.d("FollowScreen", "Followers list: $followersIds, $followerList")
                },
                onFailure = { error ->
                    Log.e("FollowScreen", "Error fetching followers: $error")
                    isDataLoaded = true
                }
            )
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.background)
            ) {
                BackArrow(
                    modifier = Modifier.align(Alignment.TopStart).padding(start = 20.dp, top = 40.dp)
                )

                Text(
                    text = name,
                    fontFamily = nunitoFontFamily,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 32.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 40.dp, bottom = 10.dp)
                )
            }

            // Tab Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            modifier = Modifier.background(MaterialTheme.colorScheme.background)
                                .height(50.dp)
                        ) {
                            Text(
                                text = title,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = nunitoFontFamily,
                                color = if (selectedTab == index)
                                    MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }

            if (selectedTab == 0) {
                if (!isDataLoaded) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center).padding(20.dp)
                        )
                    }
                } else {
                    if (followingList.isNotEmpty()) {
                        LazyColumn(modifier = Modifier.fillMaxSize().padding(10.dp)) {
                            items(followingList) { userId ->
                                UserItem(userId, onUserClick)
                            }
                        }
                    } else {
                        Text(
                            text = "You are not following anyone yet.",
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            fontSize = 16.sp,
                            fontFamily = nunitoFontFamily,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                if (!isDataLoaded) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center).padding(20.dp)
                        )
                    }
                } else {
                    if (followerList.isNotEmpty()) {
                        LazyColumn(modifier = Modifier.fillMaxSize().padding(10.dp)) {
                            items(followerList) { userId ->
                                UserItem(userId, onUserClick)
                            }
                        }
                    } else {
                        Text(
                            text = "You have no followers yet.",
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            fontSize = 16.sp,
                            fontFamily = nunitoFontFamily,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UserItem(userId: String, onUserClick: (String) -> Unit) {
    val userViewModel: UserViewModel = viewModel()
    var name by remember { mutableStateOf("") }
    var avatar by remember { mutableStateOf("") }

    LaunchedEffect(userId) {
        userViewModel.getUserById(userId,
            onSuccess = { user ->
                name = user?.name.toString()
                avatar = user?.avatarUrl.toString()
            },
            onFailure = { error -> Log.e("UserList", "Error: $error") }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onUserClick(userId)
            }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (avatar.isNotEmpty()) {
            UserAvatar(avatar, 48.dp)
        } else {
            UserWithOutAvatar(name, 24.sp, 48.dp)
        }

        Text(
            text = name,
            fontSize = 20.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = nunitoFontFamily,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
    }
}
