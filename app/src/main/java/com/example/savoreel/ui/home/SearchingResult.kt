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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.savoreel.R
import com.example.savoreel.ui.component.BackArrow
import com.example.savoreel.ui.component.CustomInputField
import com.example.savoreel.ui.component.GridImage
import com.example.savoreel.ui.theme.SavoreelTheme

@Composable
fun SearchingResult(navController: NavController, searchQuery: String) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("All", "People", "Post")
    val persons = searchresults.filter { it.type == "Person" }
    val posts = searchresults.filter { it.type == "Post" }

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
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.fillMaxWidth(),
            ) {
                BackArrow(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.align(Alignment.Top),
                )
                CustomInputField(
                    value = searchQuery,
                    onValueChange = {},
                    placeholder = "Search...",
                    modifier = Modifier
                        .height(50.dp)
                        .padding(top = 15.dp, start = 5.dp, end = 20.dp)
                )
            }

            // Tab Row for "All", "People", "Post"
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.tertiary
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
                        items(persons.size) { index ->
                            SearchResultItem(result = persons[index], onFollowClick = {})
                        }
                    }
                )
                "Post" -> GridImage(
                    posts = posts,
                    onClick = {}
                )
                "All" -> {
                    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
                        Text(
                            text = "People",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(8.dp),
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        Column(modifier = Modifier.fillMaxWidth()) {
                            val displayCount = 3
                            for (person in persons.take(displayCount)) {
                                SearchResultItem(result = person, onFollowClick = {})
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

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Posts",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(8.dp),
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        GridImage(
                            posts = posts.take(9),
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
fun SearchResultItem(result: SearchItem, onFollowClick: (SearchItem) -> Unit) {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = result.imageRes),
            contentDescription = null,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.onSurface)
        )
        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = result.name,
            style = MaterialTheme.typography.bodyMedium,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.tertiary
        )

        Button(
            onClick = {
                result.isFollowing = !result.isFollowing // Ensure state change here
                onFollowClick(result) // Trigger the callback with the updated result
            },
            modifier = Modifier
                .clip(MaterialTheme.shapes.large)
                .height(40.dp)
                .background(if (result.isFollowing) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary),
        ) {
            Icon(
                imageVector = if (result.isFollowing) Icons.Default.Check else Icons.Default.Add,
                contentDescription = null,
                tint = if (result.isFollowing) MaterialTheme.colorScheme.tertiary else Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (result.isFollowing) "Unfollow" else "Follow",
                color = if (result.isFollowing) MaterialTheme.colorScheme.tertiary else Color.White
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

data class SearchItem(
    val type: String,
    val name: String,
    val imageRes: Int,
    var isFollowing: Boolean = false
)

val searchresults = listOf(
    SearchItem("Post", "Post", R.drawable.food),
    SearchItem("Post", "Post", R.drawable.food),
    SearchItem("Post", "Post", R.drawable.food),
    SearchItem("Post", "Post", R.drawable.food),
    SearchItem("Post", "Post", R.drawable.food),
    SearchItem("Post", "Post", R.drawable.food),
    SearchItem("Post", "Post", R.drawable.food),
    SearchItem("Post", "Post", R.drawable.food),
    SearchItem("Post", "Post", R.drawable.food),
    SearchItem("Post", "Post", R.drawable.food),
    SearchItem("Post", "Post", R.drawable.food),
    SearchItem("Post", "Post", R.drawable.food),
    SearchItem("Post", "Post", R.drawable.food),
    SearchItem("Post", "Post", R.drawable.food),
    SearchItem("Post", "Post", R.drawable.food),
    SearchItem("Post", "Post", R.drawable.food),
    SearchItem("Post", "Post", R.drawable.food),
    SearchItem("Post", "Post", R.drawable.food),
    SearchItem("Post", "Post", R.drawable.food),
    SearchItem("Post", "Post", R.drawable.food),
    SearchItem("Post", "Post", R.drawable.food),
    SearchItem("Post", "Post", R.drawable.food),
    SearchItem("Post", "Post", R.drawable.food),
    SearchItem("Post", "Post", R.drawable.food),
    SearchItem("Person", "Person", R.drawable.food),
    SearchItem("Person", "Person", R.drawable.food),
    SearchItem("Person", "Person", R.drawable.food),
    SearchItem("Person", "Person", R.drawable.food),
    SearchItem("Person", "Person", R.drawable.food),
    SearchItem("Person", "Person", R.drawable.food),
    SearchItem("Person", "Person", R.drawable.food),
    SearchItem("Person", "Person", R.drawable.food),
    SearchItem("Person", "Person", R.drawable.food),
    SearchItem("Person", "Person", R.drawable.food),
    SearchItem("Person", "Person", R.drawable.food),
    SearchItem("Person", "Person", R.drawable.food),
)