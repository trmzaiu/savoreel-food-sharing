package com.example.savoreel.ui.home

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.savoreel.R
import com.example.savoreel.model.postss
import com.example.savoreel.ui.component.BackArrow
import com.example.savoreel.ui.component.CustomInputField
import com.example.savoreel.ui.component.GridImage
import com.example.savoreel.ui.theme.SavoreelTheme

@Composable
fun SearchingResult(navController: NavController, searchQuery: String) {
    var selectedTab by remember { mutableIntStateOf(2) }
    val tabs = listOf("All", "People", "Post")
    val (users, setResultList) = remember { mutableStateOf(users) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Top bar with Back Arrow and Input Field
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                BackArrow(navController = navController, modifier = Modifier.padding(start = 20.dp, top = 40.dp))
                CustomInputField(
                    value = searchQuery,
                    onValueChange = {},
                    placeholder = "Search...",
                    modifier = Modifier
                        .padding(top = 40.dp, start = 5.dp, end = 20.dp)
                )
            }

            // Tab Row for "All", "People", "Post"
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(text = title) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (tabs[selectedTab]) {
                "People" -> LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                    content = {
                        items(
                            count = users.size,
                            key = { index -> users[index].userID }
                        ) { index ->
                            val person = users[index]
                            SearchResultItem(
                                result = person,
                                onFollowClick = { updatedItem ->
                                    // Cập nhật danh sách bằng cách tạo danh sách mới với các thay đổi cần thiết
                                    setResultList(
                                        users.map { user ->
                                            if (user.userID == updatedItem.userID) {
                                                user.copy(isFollowing = !user.isFollowing)
                                            } else {
                                                user
                                            }
                                        }
                                    )
                                }
                            )
                        }

                    }
                )
                "Post" -> GridImage(
                    posts = postss,
                    onClick = {},
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                "All" -> {
                    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
                        Text(
                            text = "People",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                            val displayCount = 3
                            users.take(displayCount).forEach { person ->
                                SearchResultItem(
                                    result = person,
                                    onFollowClick = { updatedItem ->
                                        setResultList(
                                            users.map { user ->
                                                if (user.userID == updatedItem.userID) {
                                                    user.copy(isFollowing = !user.isFollowing)
                                                } else {
                                                    user
                                                }
                                            }
                                        )
                                    }
                                )
                            }
                        }


                        Text(
                            text = "See more",
                            textDecoration = TextDecoration.Underline,
                            color = Color.Gray,
                            modifier = Modifier.fillMaxWidth().clickable { selectedTab = 1 },
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall
                        )

                        Text(
                            text = "Posts",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        GridImage(
                            posts = postss.take(9),
                            onClick = {}
                        )

                        Text(
                            text = "See more",
                            textDecoration = TextDecoration.Underline,
                            color = Color.Gray,
                            modifier = Modifier.fillMaxWidth().clickable { selectedTab = 2 },
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultItem(result: User, onFollowClick: (User) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(id = result.avatar),
            contentDescription = null,
            modifier = Modifier.size(40.dp).clip(MaterialTheme.shapes.extraLarge)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = result.name,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium
        )
        Button(
            onClick = { onFollowClick(result) },
            colors = ButtonDefaults.buttonColors(containerColor = if(result.isFollowing) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary)
        ) {
            Image(
                imageVector = if (result.isFollowing) Icons.Default.Check else Icons.Default.Add,
                contentDescription = null,
                colorFilter = ColorFilter.tint(if (result.isFollowing) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onPrimary)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = if (result.isFollowing) "Unfollow" else "Follow",
                color = if (result.isFollowing) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SearchingResultPreview() {
    val mockSearchQuery = "Candy"
    val navController = rememberNavController()
    SavoreelTheme {
        SearchingResult(navController = navController, searchQuery = mockSearchQuery)
    }
}

@Preview(showBackground = true)
@Composable
fun SearchingResultPreview1() {
    val mockSearchQuery = "Candy"
    val navController = rememberNavController()
    SavoreelTheme(darkTheme = true) {
        SearchingResult(navController = navController, searchQuery = mockSearchQuery)
    }
}



data class User(
    val userID: Int,
    val name: String,
    val avatar: Int,
    var isFollowing: Boolean = false
)

var users = List(12) { i ->
    User(i + 1, "Person ${i + 1}", R.drawable.food)
}
