package com.example.savoreel.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
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
import com.example.savoreel.model.NotificationViewModel
import com.example.savoreel.model.ThemeViewModel
import com.example.savoreel.model.User
import com.example.savoreel.model.UserViewModel
import com.example.savoreel.ui.component.BackArrow
import com.example.savoreel.ui.home.GridPostActivity
import com.example.savoreel.ui.home.UserItem
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
    val notificationViewModel: NotificationViewModel = viewModel()

    var selectedTab by remember { mutableIntStateOf(if (initialTab == "following") 0 else 1) }
    var followingList by remember { mutableStateOf<List<User>>(emptyList()) }
    var followerList by remember { mutableStateOf<List<User>>(emptyList()) }
    var name by remember { mutableStateOf("") }
    var currentuid by remember {mutableStateOf("")}
    val tabs = listOf("Following", "Followers")

    // Track whether data has been loaded
    var isDataLoaded by remember { mutableStateOf(false) }
    var loadingFollowStatus by remember { mutableStateOf<Map<String, Boolean>>(emptyMap()) }

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

    val currentUser by userViewModel.user.collectAsState()

    LaunchedEffect(currentUser) {
        userViewModel.getUser(
            onSuccess = { currentUser ->
                if (currentUser != null) {
                    currentuid = currentUser.userId.toString()
                } else {
                    Log.e("ProfileScreen", "User data not found")
                }
            },
            onFailure = { error ->
                Log.e("NameTheme", "Error retrieving user: $error")
            }
        )
    }

    // When tab changes, load the corresponding data
    LaunchedEffect(selectedTab) {
        if (selectedTab == 0 && userId != null) {
            userViewModel.getFollowing(userId,
                onSuccess = { following ->
                    followingList = following
                    isDataLoaded = true
                    Log.d("FollowScreen", "Following list: $following, $followingList")
                },
                onFailure = { error ->
                    Log.e("FollowScreen", "Error fetching following: $error")
                    isDataLoaded = true
                }
            )
        } else if (selectedTab == 1 && userId != null) {
            userViewModel.getFollowers(userId,
                onSuccess = { followers ->
                    followerList = followers
                    isDataLoaded = true
                    Log.d("FollowScreen", "Followers list: $followers, $followerList")
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
                            items(followingList) { person ->
                                var isFollow by remember { mutableStateOf(false) }
                                val isLoading = loadingFollowStatus[person.userId.toString()] ?: false

                                LaunchedEffect(person.userId) {
                                    if (!loadingFollowStatus.containsKey(person.userId.toString())) {
                                        loadingFollowStatus = loadingFollowStatus + (person.userId.toString() to true)
                                        userViewModel.isFollowing(person.userId.toString(),
                                            onSuccess = { isFollowing ->
                                                isFollow = isFollowing
                                                loadingFollowStatus = loadingFollowStatus - person.userId.toString()
                                            },
                                            onFailure = { error ->
                                                Log.e("SearchScreen", "Failed to fetch follow status: $error")
                                                loadingFollowStatus = loadingFollowStatus - person.userId.toString()
                                            }
                                        )
                                    }
                                }
                                UserItem(
                                    user = person,
                                    isFollow = isFollow,
                                    isLoading = isLoading,
                                    onFollowClick = {
                                        userViewModel.toggleFollowStatus(
                                            userId = person.userId.toString(),
                                            onSuccess = { isFollowing ->
                                                if (!isFollowing) {
                                                    followingList = followingList.filter { it.userId != person.userId }
                                                } else {
                                                    val updatedList = followingList.map {
                                                        if (it.userId == person.userId) {
                                                            it.copy(following = (if (isFollowing) it.following - person.userId else it.following + person.userId) as List<String>)
                                                        } else {
                                                            it
                                                        }
                                                    }
                                                    followingList = updatedList
                                                }
                                                isFollow = isFollowing
                                                Log.d(
                                                    "SearchScreen",
                                                    "Follow status updated for ${person.name}: $isFollowing"
                                                )
                                                if (isFollowing) {
                                                    notificationViewModel.createNotification(
                                                        person.userId.toString(),
                                                        "",
                                                        "Follow",
                                                        "has started following you.",
                                                        {},
                                                        {})
                                                }
                                            },
                                            onFailure = { errorMessage ->
                                                Log.e(
                                                    "SearchScreen",
                                                    "Failed to follow/unfollow: $errorMessage"
                                                )
                                            },
                                        )
                                    },
                                    onUserClick
                                )
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
                            items(followerList) { person ->
                                var isFollow by remember { mutableStateOf(false) }
                                val isLoading = loadingFollowStatus[person.userId.toString()] ?: false

                                LaunchedEffect(person.userId) {
                                    if (!loadingFollowStatus.containsKey(person.userId.toString())) {
                                        loadingFollowStatus = loadingFollowStatus + (person.userId.toString() to true)
                                        userViewModel.isFollowing(person.userId.toString(),
                                            onSuccess = { isFollowing ->
                                                isFollow = isFollowing
                                                loadingFollowStatus = loadingFollowStatus - person.userId.toString()
                                            },
                                            onFailure = { error ->
                                                Log.e("SearchScreen", "Failed to fetch follow status: $error")
                                                loadingFollowStatus = loadingFollowStatus - person.userId.toString()
                                            }
                                        )
                                    }
                                }

                                UserItem(
                                    user = person,
                                    isFollow = isFollow,
                                    isLoading = isLoading,
                                    onFollowClick = {
                                        userViewModel.toggleFollowStatus(
                                            userId = person.userId.toString(),
                                            onSuccess = { isFollowing ->
                                                val updatedList = followerList.map {
                                                    if (it.userId == person.userId) {
                                                        it.copy(following = (if (isFollowing) it.following - person.userId else it.following + person.userId) as List<String>)
                                                    } else {
                                                        it
                                                    }
                                                }
                                                followerList = updatedList
                                                isFollow = isFollowing
                                                Log.d("FollowScreen", "Follow status updated for ${person.name}: $isFollowing")
                                                if (isFollowing) {
                                                    notificationViewModel.createNotification(
                                                        person.userId.toString(),
                                                        "",
                                                        "Follow",
                                                        "has started following you.",
                                                        {},
                                                        {}
                                                    )
                                                }
                                            },
                                            onFailure = { errorMessage ->
                                                Log.e("FollowScreen", "Failed to follow/unfollow: $errorMessage")
                                            },
                                        )
                                    },
                                    onUserClick
                                )
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