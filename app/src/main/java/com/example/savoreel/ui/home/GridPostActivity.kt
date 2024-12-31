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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.savoreel.R
import com.example.savoreel.model.NotificationViewModel
import com.example.savoreel.model.Post
import com.example.savoreel.model.PostModel
import com.example.savoreel.model.ThemeViewModel
import com.example.savoreel.model.UserViewModel
import com.example.savoreel.ui.component.ImageCustom
import com.example.savoreel.ui.component.PostTopBar
import com.example.savoreel.ui.profile.FollowActivity
import com.example.savoreel.ui.profile.UserAvatar
import com.example.savoreel.ui.profile.UserWithOutAvatar
import com.example.savoreel.ui.theme.SavoreelTheme
import com.example.savoreel.ui.theme.nunitoFontFamily

class GridPostActivity: ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()
    private val postModel: PostModel by viewModels()
    private val notificationViewModel: NotificationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        themeViewModel.loadUserSettings()
        postModel.getFollowingUserIds()
        notificationViewModel.countUnreadNotifications()

        setContent {
            val isDarkMode by themeViewModel.isDarkModeEnabled.collectAsState()
            val userID = intent.getStringExtra("USER_ID") ?: "Everyone"
            SavoreelTheme(darkTheme = isDarkMode) {
                GridPost(userID = userID)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GridPost(userID: String) {
    val postModel: PostModel = viewModel()
    val userViewModel: UserViewModel = viewModel()
    var isRowVisible by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf("") }
    var uid by remember { mutableStateOf("") }
    var avatarUrl by remember { mutableStateOf("") }
    var numberOfFollower by remember { mutableIntStateOf(0) }
    var numberOfFollowing by remember { mutableIntStateOf(0) }
    var currentUserName by remember { mutableStateOf("") }
    var currentUserAvatar by remember { mutableStateOf("") }
    var listOfPost by remember { mutableStateOf(emptyList<Post>()) }
    val context = LocalContext.current
    val posts by postModel.posts.collectAsState(emptyList())
    val currentUser by userViewModel.user.collectAsState()

    LaunchedEffect(currentUser) {
        userViewModel.getUser(
            onSuccess = { currentUser ->
                if (currentUser != null) {
                    currentUserName = currentUser.name.toString()
                    currentUserAvatar = currentUser.avatarUrl.toString()
                } else {
                    Log.e("ProfileScreen", "User data not found")
                }
            },
            onFailure = { error ->
                Log.e("NameTheme", "Error retrieving user: $error")
            }
        )
    }

    LaunchedEffect(posts) {
        postModel.getPostsFromFirebase(
            onSuccess = {
                listOfPost = posts
            },
            onFailure = {}
        )
    }

    LaunchedEffect(Unit) {
        userViewModel.getUserById(
            userId = userID,
            onSuccess = { user ->
                if (user != null) {
                    name = user.name.toString()
                    uid = user.userId.toString()
                    avatarUrl = user.avatarUrl.toString()
                    numberOfFollower = user.followers.size
                    numberOfFollowing = user.following.size
                } else {
                    Log.e("GridPost", "User data not found")
                }
            },
            onFailure = { error ->
                Log.e("GridPost", "Error retrieving user: $error")
            }
        )
        if (userID != "Everyone") {
            isRowVisible = true
            postModel.getPostsFromUserId(
                userID,
                onSuccess = { posts ->
                    listOfPost = posts
                },
                onFailure = {
                }
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        Column {
            PostTopBar(currentUserAvatar, currentUserName)

            if (isRowVisible) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (avatarUrl.isNotEmpty()) {
                            UserAvatar(avatarUrl, 100.dp)
                        } else {
                            UserWithOutAvatar(name, 60.sp, 100.dp)
                        }
                        Text(
                            text = name,
                            fontFamily = nunitoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(top = 5.dp)
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                val intent = Intent(context, FollowActivity::class.java).apply {
                                    putExtra("USER_ID", userID)
                                    putExtra("TAB", "following")
                                }
                                context.startActivity(intent)
                            }
                    ) {
                        Text(
                            text = numberOfFollowing.toString(),
                            fontFamily = nunitoFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        Text(
                            text = "Following",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                val intent = Intent(context, FollowActivity::class.java).apply {
                                    putExtra("USER_ID", userID)
                                    putExtra("TAB", "follower")
                                }
                                context.startActivity(intent)
                            }
                    ) {
                        Text(
                            text = numberOfFollower.toString(),
                            fontFamily = nunitoFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        Text(
                            text = "Followers",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                }
            }

            GridImage(posts = listOfPost, onClick = { post ->
                val intent = Intent(context, PostActivity::class.java).apply {
                    putExtra("POST_ID", post.postId)
                }
                context.startActivity(intent)
            })
        }
    }
}

@Composable
fun GridImage(posts: List<Post>, onClick: (Post) -> Unit, modifier: Modifier = Modifier){
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 100.dp),
        contentPadding = PaddingValues(5.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = modifier
    ) {
        items(posts) { post ->
            ImageCustom(
                url = post.photoUri,
                post = post,
                onClick = onClick
            )
        }
    }
}

@Composable
fun PostImage(
    url: String,
    modifier: Modifier = Modifier
) {
    val secureUrl = url.replace("http://", "https://") // Use the replaced URL
    val context = LocalContext.current

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(secureUrl) // Pass the secure URL
            .crossfade(true)
            .build()
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        when (painter.state) {
            is AsyncImagePainter.State.Error -> {
                Image(
                    painter = painterResource(R.drawable.avatar_error),
                    contentDescription = "Error loading post",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            else -> {
                Image(
                    painter = painter,
                    contentDescription = "Post image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}