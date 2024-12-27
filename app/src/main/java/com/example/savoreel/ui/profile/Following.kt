package com.example.savoreel.ui.profile

import android.util.Log
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.savoreel.model.User
import com.example.savoreel.model.UserViewModel
import com.example.savoreel.ui.component.BackArrow
import com.example.savoreel.ui.theme.SavoreelTheme
import com.example.savoreel.ui.theme.nunitoFontFamily

@Composable
fun FollowScreen(navController: NavController, userViewModel: UserViewModel, query: String, userid: String) {
    // Make sure selectedTab state is mutable and is being properly updated
    var selectedTab by remember { mutableStateOf(if (query == "following") 0 else 1) }
    val followingList = remember { mutableListOf<String>() }
    val followerList = remember { mutableListOf<String>() }
    val isDarkModeEnabled by rememberSaveable { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var imgUrl by remember { mutableStateOf("") }
    val tabs = listOf("Following", "Followers")

    // Track whether data has been loaded
    var isDataLoaded by remember { mutableStateOf(false) }

    // When tab changes, load the corresponding data
    LaunchedEffect(selectedTab) {
        if (selectedTab == 0) {
            // Load following list
            userViewModel.getUserById(
                userid,
                onSuccess = { user ->
                    if (user != null) {
                        followingList.clear()
                        followingList.addAll(user.following)
                        isDataLoaded = true // Mark data as loaded
                    }
                },
                onFailure = { error ->
                    Log.e("FollowScreen", "Error retrieving user: $error")
                }
            )
        } else {
            // Load follower list
            userViewModel.getUserById(
                userid,
                onSuccess = { user ->
                    if (user != null) {
                        followerList.clear()
                        followerList.addAll(user.followers)
                        isDataLoaded = true // Mark data as loaded
                    }
                },
                onFailure = { error ->
                    Log.e("FollowScreen", "Error retrieving user: $error")
                }
            )
        }
    }

    // Fetch user information once
    LaunchedEffect(Unit) {
        userViewModel.getUserById(
            userid,
            onSuccess = { user ->
                if (user != null) {
                    name = user.name.toString()
                    imgUrl = user.avatarUrl.toString()
                } else {
                    Log.e("FollowScreen", "User data not found")
                }
            },
            onFailure = { error ->
                Log.e("FollowScreen", "Error retrieving user: $error")
            }
        )
    }

    SavoreelTheme(darkTheme = isDarkModeEnabled) {
        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 40.dp, bottom = 20.dp)
                        .background(color = MaterialTheme.colorScheme.background),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BackArrow(
                        navController = navController,
                    )
                    Text(
                        text = name,
                        fontFamily = nunitoFontFamily,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.onBackground,
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

                // Display appropriate user list based on selected tab
                if (!isDataLoaded) {
                    Text("Loading...")
                } else {
                    val users = if (selectedTab == 0) followingList else followerList
                    UserList(users = users, userViewModel = userViewModel, navController = navController, selectedTab = selectedTab)
                }
            }
        }
    }
}

@Composable
fun UserList(users: List<String>, userViewModel: UserViewModel, navController: NavController, selectedTab: Int) {
    val persons = remember { mutableStateListOf<User>() }
    var name by remember {mutableStateOf("")}
    LaunchedEffect(Unit) {
        userViewModel.getUser(onSuccess = { user ->
            if (user != null) {
                name = user.name.toString()
            } else {
                Log.e("Following", "User data not found")
            }
        }, onFailure = { error ->
            Log.e("Following", "Error retrieving user: $error")
        })
    }
    LaunchedEffect(users) {
        persons.clear()
        userViewModel.getUsersByIds(users,
            onSuccess = { usersList -> persons.addAll(usersList) },
            onFailure = { error -> Log.e("UserList", "Error: $error") }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (!persons.isEmpty()) {
            items(persons.size) { index ->
                UserItem(persons[index], navController)
            }
        } else {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (selectedTab == 0) "$name's not following anyone yet"
                        else "No followers yet",
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

@Composable
fun UserItem(user: User, navController: NavController) {
    val userid = user.userId
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                navController.navigate("grid_post/$userid")
            }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "User Avatar",
            tint = Color.Gray,
            modifier = Modifier.size(48.dp)
        )
        Text(
            text = user.name.toString(),
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
