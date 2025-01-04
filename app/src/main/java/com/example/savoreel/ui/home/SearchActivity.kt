@file:Suppress("UNCHECKED_CAST")

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter.Companion.tint
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
import com.example.savoreel.model.NotificationViewModel
import com.example.savoreel.model.Post
import com.example.savoreel.model.PostModel
import com.example.savoreel.model.ThemeViewModel
import com.example.savoreel.model.User
import com.example.savoreel.model.UserViewModel
import com.example.savoreel.ui.component.BackArrow
import com.example.savoreel.ui.profile.UserAvatar
import com.example.savoreel.ui.profile.UserWithOutAvatar
import com.example.savoreel.ui.theme.SavoreelTheme
import com.example.savoreel.ui.theme.nunitoFontFamily
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

class SearchActivity: ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        themeViewModel.loadUserSettings()

        setContent {
            val isDarkMode by themeViewModel.isDarkModeEnabled.collectAsState()
            SavoreelTheme(darkTheme = isDarkMode) {
                val initialQuery = intent.getStringExtra("initialQuery") ?:""
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
    val postModel: PostModel = viewModel()
    val notificationViewModel: NotificationViewModel = viewModel()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    var searchQuery by remember { mutableStateOf(initialQuery) }
    var selectedTab by remember { mutableIntStateOf(0) }
    var showSuggestions by remember { mutableStateOf(searchQuery.isEmpty()) }
    var recentSearches by remember { mutableStateOf<List<String>>(emptyList()) }
    var suggestion by remember { mutableStateOf<List<String>>(emptyList()) }
    val tabs = listOf("All", "People", "Post")

    var persons by remember { mutableStateOf(emptyList<User>()) }
    var postsWithHashtag by remember { mutableStateOf(emptyList<Post>()) }
    var postsWithTitle by remember { mutableStateOf(emptyList<Post>()) }
    var loadingFollowStatus by remember { mutableStateOf<Map<String, Boolean>>(emptyMap()) }

    var isLoadingPeople by remember { mutableStateOf(true) }
    var isLoadingPosts by remember { mutableStateOf(true) }
    val context = LocalContext.current
    // Fetch recent search history
    LaunchedEffect(Unit) {
        userViewModel.getSearchHistory(
            onSuccess = { searchHistory ->
                recentSearches = searchHistory.map { it.keyword.toString() }
            },
            onFailure = { error ->
                Log.e("Search", "Failed to fetch search history: $error")
            }
        )
    }

    LaunchedEffect(Unit) {
        userViewModel.getHashtags(
            onSuccess = { hashtags ->
                suggestion = hashtags
            },
            onFailure = { error ->
                Log.e("Search", "Failed to fetch hashtags: $error")
            }
        )
    }

    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotEmpty()) {
            isLoadingPeople = true
            isLoadingPosts = true
            val fetchUsersDeferred = async {
                userViewModel.getAllUsersByNameKeyword(
                    searchQuery,
                    onSuccess = { users ->
                        persons = users.filter { user ->
                            user.name?.contains(searchQuery, ignoreCase = true) == true
                        }
                        isLoadingPeople = false
                    },
                    onFailure = { error ->
                        Log.e("Search", "Failed to fetch users: $error")
                        isLoadingPeople = false
                    }
                )
            }

            val postsWithHashtagDeferred = async {
                postModel.searchPostByHashtag(
                    searchQuery,
                    onSuccess = { posts ->
                        // Filter posts by hashtag (case-insensitive)
                        postsWithHashtag = posts.filter { post ->
                            post.hashtag?.any { it.contains(searchQuery, ignoreCase = true) } == true
                        }
                        isLoadingPosts = false
                        Log.d("Search", "Posts with hashtag: ${postsWithHashtag.size}")
                    },
                    onFailure = { error ->
                        Log.e("Search", "Failed to fetch posts by hashtag: $error")
                        isLoadingPosts = false
                    }
                )
            }

            val postsWithTitleDeferred = async {
                postModel.searchPostByTitle(
                    searchQuery,
                    onSuccess = { posts ->
                        postsWithTitle = posts.filter { post ->
                            post.title?.contains(searchQuery, ignoreCase = true) == true
                        }
                        Log.d("Search", "Posts with title: ${postsWithTitle.size}")
                        isLoadingPosts = false
                    },
                    onFailure = { error ->
                        Log.e("Search", "Failed to fetch posts by title: $error")
                        isLoadingPosts = false
                    }
                )
            }

            awaitAll(fetchUsersDeferred ,postsWithHashtagDeferred, postsWithTitleDeferred)
        } else {
            persons = emptyList()
            postsWithHashtag = emptyList()
            postsWithTitle = emptyList()
            isLoadingPeople = false
            isLoadingPosts = false
        }
    }

    val posts = remember(postsWithHashtag, postsWithTitle) {
        (postsWithHashtag + postsWithTitle).distinctBy { it.postId }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 40.dp, bottom = 20.dp),
            ) {
                BackArrow()
                Box(
                    modifier = Modifier
                        .height(50.dp)
                        .background(MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(15.dp))
                        .padding(horizontal = 10.dp)
                ) {
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            showSuggestions = searchQuery.isEmpty()
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxSize(),
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            fontFamily = nunitoFontFamily,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = {
                            if (searchQuery.isNotBlank()) {
                                userViewModel.updateSearchHistory(searchQuery, onSuccess = {
                                    searchResult()
                                    keyboardController?.hide()
                                    focusManager.clearFocus()
                                }, onFailure = { errorMessage ->
                                    Log.e("Search", "Failed to save search history: $errorMessage")
                                    keyboardController?.hide()
                                    focusManager.clearFocus()
                                })
                                if (searchQuery.startsWith("#")) {
                                    userViewModel.updateUserHashtags(searchQuery, onSuccess = {
                                        Log.d("Search", "Hashtag saved successfully.")
                                    }, onFailure = { errorMessage ->
                                        Log.e("Search", "Failed to save hashtag: $errorMessage")
                                    })
                                }
                            }
                        }),
                        decorationBox = { innerTextField ->
                            Box(
                                contentAlignment = Alignment.CenterStart,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        text = "Search",
                                        fontSize = 16.sp,
                                        fontFamily = nunitoFontFamily,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }

            }

            // Show suggestions if search query is empty
            if (showSuggestions) {
                Column (modifier = Modifier.padding(start = 20.dp, top = 10.dp)) {
                    if (recentSearches.isNotEmpty()) {
                        SearchCategory(
                            title = "Recent search",
                            items = recentSearches,
                            isSuggestion = false,
                            onItemClick = { selectedItem ->
                                userViewModel.updateSearchHistory(selectedItem, onSuccess = {
                                    searchResult()
                                    keyboardController?.hide()
                                    focusManager.clearFocus()
                                }, onFailure = { errorMessage ->
                                    Log.e("Search", "Failed to save search history: $errorMessage")
                                    keyboardController?.hide()
                                    focusManager.clearFocus()
                                })
                                showSuggestions = false
                                searchQuery = selectedItem
                            }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    SearchCategory(
                        title = "Suggestion for you",
                        items = suggestion,
                        isSuggestion = true,
                        onItemClick = { selectedItem ->
                            userViewModel.updateSearchHistory(selectedItem, onSuccess = {
                                searchResult()
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            }, onFailure = { errorMessage ->
                                Log.e("Search", "Failed to save search history: $errorMessage")
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            })
                            showSuggestions = false
                            searchQuery = selectedItem
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
                    "People" ->
                        if (persons.isEmpty()) {
                            // Show a "No results found" message when no users are found
                            Text(
                                text = "No results found",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(horizontal = 15.dp, vertical = 5.dp),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        } else {
                            LazyColumn(modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 10.dp),
                            ) {
                                items(persons) { person ->
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
                                                    val updatedList = persons.map {
                                                        if (it.userId == person.userId) {
                                                            it.copy(following = (if (isFollowing) it.following - person.userId else it.following + person.userId) as List<String>)
                                                        } else {
                                                            it
                                                        }
                                                    }
                                                    persons = updatedList
                                                    isFollow = isFollowing
                                                    Log.d("SearchScreen", "Follow status updated for ${person.name}: $isFollowing")
                                                    if (isFollowing) {
                                                        notificationViewModel.createNotification(person.userId.toString(), "","Follow", "has started following you.", {}, {})
                                                    }
                                                },
                                                onFailure = { errorMessage ->
                                                    Log.e("SearchScreen", "Failed to follow/unfollow: $errorMessage")
                                                },
                                            )
                                        },
                                        onUserClick
                                    )
                                }
                            }
                        }

                    "Post" ->
                        if (posts.isEmpty()) {
                            Text(
                                text = "No results found",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(horizontal = 20.dp),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        } else {
                            GridImage(
                                posts = posts,
                                onClick = {post ->
                                    val intent = Intent(context, PostActivity::class.java).apply {
                                        putExtra("POST_ID", post.postId)
                                    }
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )
                        }
                    "All" -> {
                        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp)) {
                            // People section
                            Text(
                                text = "People",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSecondary
                            )

                            if (isLoadingPeople) {
                                CircularProgressIndicator(
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            } else {
                                if (persons.isNotEmpty()) {
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        persons.take(3).forEach { person ->
                                            var isFollow by remember { mutableStateOf(false) }
                                            val isLoading = loadingFollowStatus[person.userId.toString()] ?: false

                                            LaunchedEffect(person.userId) {
                                                if (!loadingFollowStatus.containsKey(person.userId.toString())) {
                                                    loadingFollowStatus = loadingFollowStatus + (person.userId.toString() to true)
                                                    userViewModel.isFollowing(person.userId.toString(),
                                                        onSuccess = { isFollowing ->
                                                            isFollow = isFollowing
                                                            loadingFollowStatus = loadingFollowStatus - person.userId.toString() // Remove loading state
                                                        },
                                                        onFailure = { error ->
                                                            Log.e("SearchScreen", "Failed to fetch follow status: $error")
                                                            loadingFollowStatus = loadingFollowStatus - person.userId.toString() // Remove loading state
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
                                                            val updatedList = persons.map {
                                                                if (it.userId == person.userId) {
                                                                    it.copy(following = (if (isFollowing) it.following - person.userId else it.following + person.userId) as List<String>)
                                                                } else {
                                                                    it
                                                                }
                                                            }
                                                            persons = updatedList
                                                            isFollow = isFollowing
                                                            Log.d("SearchScreen", "Follow status updated for ${person.name}: $isFollowing")
                                                            if (isFollowing) {
                                                                notificationViewModel.createNotification(person.userId.toString(), "", "Follow", "has started following you.", {}, {})
                                                            }
                                                        },
                                                        onFailure = { errorMessage ->
                                                            Log.e("SearchScreen", "Failed to follow/unfollow: $errorMessage")
                                                        },
                                                    )
                                                },
                                                onUserClick
                                            )
                                        }
                                    }
                                    if (persons.size > 3) {
                                        Text(
                                            text = "See more",
                                            modifier = Modifier
                                                .fillMaxWidth().clickable { selectedTab = 1 },
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray,
                                            textDecoration = TextDecoration.Underline,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                } else {
                                    Text(
                                        text = "No results found",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(horizontal = 20.dp),
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Posts section
                            Text(
                                text = "Posts",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = MaterialTheme.colorScheme.onSecondary
                            )

                            if (isLoadingPosts) {
                                CircularProgressIndicator(
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            } else {
                                if (posts.isEmpty()) {
                                    Text(
                                        text = "No results found",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(horizontal = 20.dp),
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                } else {
                                    GridImage(
                                        posts = posts.take(9),
                                        onClick = { post ->
                                            val intent = Intent(context, PostActivity::class.java).apply {
                                                putExtra("POST_ID", post.postId)
                                            }
                                            context.startActivity(intent)
                                        }
                                    )

                                    if (posts.size > 9) {
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
        }
    }
}

@Composable
fun UserItem(user: User, isFollow: Boolean, isLoading: Boolean, onFollowClick: (User) -> Unit, onUserClick: (String) -> Unit){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onUserClick(user.userId.toString())
                Log.d("UserItem", "User clicked: ${user.name}")
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (user.avatarUrl.toString().isNotEmpty()) {
            UserAvatar(user.avatarUrl.toString(), 40.dp)
        } else {
            UserWithOutAvatar(user.name.toString(), 24.sp, 40.dp)
        }

        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = user.name.toString(),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp))
        } else {
            Button(
                onClick = {
                    onFollowClick(user)
                    Log.d("UserItem", "Follow button clicked for: ${user.name}")
                },
                colors = ButtonDefaults.buttonColors(containerColor = if(isFollow) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary)
            ) {
                Image(
                    imageVector = if (isFollow) Icons.Default.Check else Icons.Default.Add,
                    contentDescription = null,
                    colorFilter = tint(if (isFollow) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onPrimary)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = if (isFollow) "Unfollow" else "Follow",
                    color = if (isFollow) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onPrimary
                )
            }
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
            color = MaterialTheme.colorScheme.onSecondary
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