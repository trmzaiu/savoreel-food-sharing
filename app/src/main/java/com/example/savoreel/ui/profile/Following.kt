package com.example.savoreel.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.savoreel.R
import com.example.savoreel.ui.component.BackArrow
import com.example.savoreel.ui.theme.SavoreelTheme
import com.example.savoreel.ui.theme.nunitoFontFamily

@Composable
fun FollowScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf(0) } // Quản lý trạng thái nút
    val followingList = getFollowingData()
    val followerList = getFollowerData()
    val tabs = listOf("Following", "Follower")
    var isDarkModeEnabled by rememberSaveable { mutableStateOf(false) }  // Add this state
    SavoreelTheme(darkTheme = isDarkModeEnabled) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background) // Đặt nền màu sáng
                .padding(horizontal = 20.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                BackArrow(
                    navController = navController,
                    modifier = Modifier.align(Alignment.TopStart)
                )
                Text(
                    text = "Name",
                    fontFamily = nunitoFontFamily,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 32.sp,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 40.dp)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
            // Button Group
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.tertiary
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = nunitoFontFamily,
                                    color = if (selectedTab == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
                                ) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // User List based on tab
                val items = if (selectedTab == 0) followingList else followerList
                UserList(users = items)
            }
        }
    }
}

@Composable
fun UserList(users: List<User>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(users.size) { index ->
            UserItem(user = users[index])
        }
    }
}

@Composable
fun UserItem(user: User) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "User Avatar",
            tint = Color.Gray,
            modifier = Modifier
                .size(48.dp)
        )

        // Tên người dùng
        Text(
            text = user.name,
            fontSize = 20.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = nunitoFontFamily,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { /* Thêm hành động khi bấm vào user */ }
                .padding(horizontal = 16.dp)
        )
    }
}

// Hàm trả về danh sách Following
fun getFollowingData(): List<User> {
    return listOf(
        User("User 1", R.drawable.food),
        User("User 2", R.drawable.food),
        User("User 3", R.drawable.food),
        User("User 4", R.drawable.food),
        User("User 5", R.drawable.food),
        User("User 6", R.drawable.food),
        User("User 7", R.drawable.food),
        User("User 8", R.drawable.food),
        User("User 9", R.drawable.food),
        User("User 10", R.drawable.food),
        User("User 11", R.drawable.food),
        User("User 12", R.drawable.food)
    )
}

// Hàm trả về danh sách Follower
fun getFollowerData(): List<User> {
    return listOf(
        User("User A", R.drawable.food),
        User("User B", R.drawable.food),
        User("User C", R.drawable.food)
    )
}

// Data class for user information
data class User(val name: String, val avatarRes: Int)

@Preview(showBackground = true)
@Composable
fun FollowScreenPreview() {
    FollowScreen(navController = rememberNavController())
}
