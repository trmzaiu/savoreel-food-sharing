package com.example.savoreel.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.navigation.NavController
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.example.savoreel.R
import com.example.savoreel.ui.component.BackArrow
import com.example.savoreel.ui.theme.backgroundLightColor
import com.example.savoreel.ui.theme.nunitoFontFamily
import com.example.savoreel.ui.theme.primaryButtonColor

@Composable
fun FollowScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf("Following") } // Quản lý trạng thái nút
    val followingList = getFollowingData()
    val followerList = getFollowerData()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundLightColor) // Đặt nền màu sáng
            .padding(16.dp)
    ) {
        BackArrow(
            modifier = Modifier,
            onClick = { navController.popBackStack() }
        )

            // Centered Name
        Text(
            text = "Name",
            fontFamily = nunitoFontFamily,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            fontSize = 32.sp,
            modifier = Modifier
                .fillMaxWidth() // Weight for centering logic
        )

        Spacer(modifier = Modifier.height(30.dp))
        // Button Group
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TabButton(
                text = "Following",
                isSelected = selectedTab == "Following",
                onClick = { selectedTab = "Following" }
            )

            TabButton(
                text = "Follower",
                isSelected = selectedTab == "Follower",
                onClick = { selectedTab = "Follower" }
            )
        }
        // Danh sách hiển thị
        val items = if (selectedTab == "Following") followingList else followerList
        UserList(users = items)
    }
}

@Composable
fun TabButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick() }
            .background(Color.Transparent)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = text,
                fontSize = 16.sp,
                fontFamily = nunitoFontFamily,
                fontWeight = FontWeight.Normal,
                color = if (isSelected) primaryButtonColor else Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth()
                    .background(if (isSelected) primaryButtonColor else Color.Transparent)
            )
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
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = nunitoFontFamily,
            color = Color.Black,
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
