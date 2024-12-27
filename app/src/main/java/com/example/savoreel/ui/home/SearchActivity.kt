package com.example.savoreel.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter.Companion.tint
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.savoreel.R
import com.example.savoreel.model.ThemeViewModel
import com.example.savoreel.model.User
import com.example.savoreel.model.UserViewModel
import com.example.savoreel.model.postss
import com.example.savoreel.ui.component.BackArrow
import com.example.savoreel.ui.component.ImageFromUrl
import com.example.savoreel.ui.theme.SavoreelTheme
import com.example.savoreel.ui.theme.nunitoFontFamily

class SearchActivity: ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        themeViewModel.loadUserSettings()

        setContent {
            val isDarkMode by themeViewModel.isDarkModeEnabled.observeAsState(initial = false)
            SavoreelTheme(darkTheme = isDarkMode) {
                val initialQuery = intent.getStringExtra("initialQuery") ?:""
//                val item = intent.getStringExtra("item") ?:""
                SearchScreen(
                    initialQuery = initialQuery,
                    searchResult = {
                        if (initialQuery.isNotBlank()) {
                            val intent = Intent(this, SearchActivity:: class.java).apply {
                                putExtra("initialQuery", initialQuery)
                            }
                            startActivity(intent)
                        }
                }){ userId ->
                    val intent = Intent(this, GridPostActivity::class.java).apply {
                        putExtra("USER_ID", userId)
                    }
                    startActivity(intent)
                }
            }
        }
    }
}

@Composable
fun SearchScreen(initialQuery: String, searchResult: () -> Unit, onUserClick: (String) -> Unit) {
    val userViewModel: UserViewModel = viewModel()

    var searchQuery by remember { mutableStateOf(initialQuery) }
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("All", "People", "Post")

    var persons by remember { mutableStateOf(emptyList<User>()) }

    userViewModel.getAllUsersByNameKeyword(
        searchQuery,
        onSuccess = { users ->
            persons = users
        },
        onFailure = { /* Handle error */ }
    )

    var showSuggestions by remember { mutableStateOf(searchQuery.isEmpty()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Search bar with back arrow
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 40.dp, bottom = 20.dp),
            ) {
                BackArrow()
                TextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        showSuggestions = it.isEmpty()
                    },
                    modifier = Modifier
                        .height(48.dp)
                        .background(
                            color = MaterialTheme.colorScheme.secondary,
                            shape = RoundedCornerShape(15.dp)
                        )
                        .padding(horizontal = 10.dp),
                    label = null, // Remove default label to mimic placeholder
                    placeholder = {
                        Text(
                            text = "Search",
                            fontSize = 16.sp,
                            fontFamily = nunitoFontFamily,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.height(40.dp)
                        )
                    },
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        fontFamily = nunitoFontFamily,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground,
                        lineHeight = 24.sp
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        if (searchQuery.isNotBlank()) {
                            searchResult()
                        }
                    }),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.secondary,
                        focusedContainerColor = MaterialTheme.colorScheme.secondary,
                        cursorColor = MaterialTheme.colorScheme.onBackground,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(40)
                )
            }

            // Show suggestions if search query is empty
            if (showSuggestions) {
                Column (modifier = Modifier.padding(start = 20.dp, top = 10.dp)) {
                    SearchCategory(
                        title = "Recent search",
                        items = listOf(
                            "vietnamese",
                            "vegetarian",
                            "korean",
                            "tiramisu",
                            "fastfood",
                            "bunbo",
                            "buffet",
                            "seafood"
                        ),
                        isSuggestion = false,
                        onItemClick = { selectedItem ->
                            showSuggestions = false
                            searchQuery = selectedItem
                            searchResult()
                        }
                    )

                    SearchCategory(
                        title = "Suggestion for you",
                        items = listOf(
                            "vietnamese",
                            "vegetarian",
                            "korean",
                            "tiramisu",
                            "fastfood",
                            "bunbo",
                            "buffet",
                            "seafood"
                        ),
                        isSuggestion = true,
                        onItemClick = { selectedItem ->
                            showSuggestions = false
                            searchQuery = selectedItem
                            searchResult()
                        }
                    )
                }
            } else {
                // Tab Row for "All", "People", "Post"
                TabRow(
                    selectedTabIndex = selectedTab,
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
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        content = {
                            items(persons.size) { i ->
                                var user = persons[i]
                                var isFolow by remember { mutableStateOf(false) }
                                userViewModel.isFollowing(user.userId.toString(),
                                    onSuccess = { isFollowing ->
                                        isFolow = isFollowing
                                    },
                                    onFailure = { errorMessage ->
                                        // Handle failure
                                    }
                                )
                                SearchResultItem(
                                    user = user,
                                    onFollowClick = { person ->
                                        userViewModel.toggleFollowStatus(
                                            userId = user.userId.toString(),
                                            onSuccess = { isFollowing ->
                                                user = person.copy(following = person.following)
                                            },
                                            onFailure = { errorMessage ->
                                                // Handle failure
                                            }
                                        )
                                    },
                                    onUserClick = onUserClick
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
                        Column(modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp)) {
                            // People section
                            Text(
                                text = "People",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            persons.take(3).forEachIndexed { i, user ->
                                var us = user
                                var isFolow by remember { mutableStateOf(false) }
                                userViewModel.isFollowing(user.userId.toString(),
                                    onSuccess = { isFollowing ->
                                        isFolow = isFollowing
                                    },
                                    onFailure = { errorMessage ->
                                        // Handle failure
                                    }
                                )
                                SearchResultItem(
                                    user = user,
                                    onFollowClick = { person ->
                                        userViewModel.toggleFollowStatus(
                                            userId = user.userId.toString(),
                                            onSuccess = { isFollowing ->
                                                us = person.copy(following = person.following)
                                            },
                                            onFailure = { errorMessage ->
                                                // Handle failure
                                            }
                                        )
                                    },
                                    onUserClick = onUserClick
                                )
                            }


                            Text(
                                text = "See more",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedTab = 1 },
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                textDecoration = TextDecoration.Underline,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Posts section
                            Text(
                                text = "Posts",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            GridImage(posts = postss.take(9), onClick = {})
                            Text(
                                text = "See more",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedTab = 2 },
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                textDecoration = TextDecoration.Underline,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultItem(user: User, onFollowClick: (User) -> Unit, onUserClick: (String) -> Unit){
    val userViewModel: UserViewModel = viewModel()

    var isFollowed by remember { mutableStateOf(false) }

    LaunchedEffect(user.userId) {
        userViewModel.isFollowing(user.userId.toString(),
            onSuccess = { isFollowing ->
                isFollowed = isFollowing
            },
            onFailure = { errorMessage ->
                // Handle failure
            }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onUserClick(user.userId.toString())
                Log.d("UserItem", user.userId.toString())
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ImageFromUrl(
            url = user.avatarUrl.toString(),
            modifier = Modifier
                .size(40.dp)
                .clip(MaterialTheme.shapes.extraLarge)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = user.name.toString(),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium
        )
        Button(
            onClick = {
                onFollowClick(user)
                userViewModel.toggleFollowStatus(
                    userId = user.userId.toString(),
                    onSuccess = { isFollowing ->
                        isFollowed = isFollowing
                    },
                    onFailure = { errorMessage ->
                        // Handle failure
                    }
                )
            },
            colors = ButtonDefaults.buttonColors(containerColor = if(isFollowed) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary)
        ) {
            Image(
                imageVector = if (isFollowed) Icons.Default.Check else Icons.Default.Add,
                contentDescription = null,
                colorFilter = tint(if (isFollowed) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onPrimary)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = if (isFollowed) "Unfollow" else "Follow",
                color = if (isFollowed) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchCategory(
    title: String,
    items: List<String>,
    isSuggestion: Boolean,
    onItemClick: (String) -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp),
            color = MaterialTheme.colorScheme.onBackground
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(),
            maxItemsInEachRow = 4
        ) {
            items.forEach { item ->
                Box(
                    modifier = Modifier
                        .clickable (
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onItemClick(item) }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondary)
                            .padding(horizontal = 12.dp)
                            .height(40.dp)
                    ) {
                        Image(
                            painter = painterResource(if (isSuggestion) R.drawable.ic_increase_red else R.drawable.ic_history),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            colorFilter = tint(if (isSuggestion) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground)
                        )

                        Spacer(modifier = Modifier.width(5.dp))

                        Text(
                            text = item,
                            color = if (isSuggestion) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}