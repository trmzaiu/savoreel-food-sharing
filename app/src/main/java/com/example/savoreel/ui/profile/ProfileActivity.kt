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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.savoreel.R
import com.example.savoreel.model.Post
import com.example.savoreel.model.PostModel
import com.example.savoreel.model.ThemeViewModel
import com.example.savoreel.model.UserViewModel
import com.example.savoreel.ui.component.BackArrow
import com.example.savoreel.ui.component.NavButton
import com.example.savoreel.ui.home.PostActivity
import com.example.savoreel.ui.home.PostImage
import com.example.savoreel.ui.setting.SettingActivity
import com.example.savoreel.ui.theme.SavoreelTheme
import com.example.savoreel.ui.theme.nunitoFontFamily
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class ProfileActivity : ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        themeViewModel.loadUserSettings()

        setContent {
            val isDarkMode by themeViewModel.isDarkModeEnabled.collectAsState()

            SavoreelTheme(darkTheme = isDarkMode) {
                ProfileScreen(
                    navigateToSetting = {
                        val intent = Intent(this, SettingActivity::class.java)
                        startActivity(intent)
                    },
                    navigateToFollow = { tab, userId ->
                        val intent = Intent(this, FollowActivity::class.java)
                        intent.putExtra("TAB", tab)
                        intent.putExtra("USER_ID", userId)
                        startActivity(intent)
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        themeViewModel.loadUserSettings()
        userViewModel.getUser(
            onSuccess = { user -> userViewModel.setUser(user) },
            onFailure = { error -> Log.e("ProfileActivity", "Error retrieving user: $error") }
        )
    }
}

@Composable
fun ProfileScreen(navigateToSetting: () -> Unit, navigateToFollow: (String, String) -> Unit) {
    val postModel: PostModel = viewModel()
    val userViewModel: UserViewModel = viewModel()

    val listState = rememberLazyListState()
    var isRowVisible by remember { mutableStateOf(true) }
    var numberOfFollower by remember { mutableIntStateOf(0) }
    var numberOfFollowing by remember { mutableIntStateOf(0) }
    var name by remember { mutableStateOf("") }
    var imgUrl by remember { mutableStateOf("") }
    var uid by remember {mutableStateOf("")}
    var isLoading by remember { mutableStateOf(true) }
    var lisOfPost by remember { mutableStateOf(emptyList<Post>()) }
    var groupedPosts by remember { mutableStateOf(emptyMap<String, List<Post>>()) }

    val currentUser by userViewModel.user.collectAsState()

    LaunchedEffect(currentUser) {
        userViewModel.getUser(
            onSuccess = { currentUser ->
                if (currentUser != null) {
                    uid = currentUser.userId.toString()
                    name = currentUser.name.toString()
                    imgUrl = currentUser.avatarUrl.toString()
                    numberOfFollower = currentUser.followers.size
                    numberOfFollowing = currentUser.following.size
                } else {
                    Log.e("ProfileScreen", "User data not found")
                }
                isLoading = false
            },
            onFailure = { error ->
                Log.e("NameTheme", "Error retrieving user: $error")
                isLoading = false
            }
        )
    }

    LaunchedEffect(Unit) {
        postModel.getPostsFromCurrentUser(
            onSuccess = { posts ->
                lisOfPost = posts
                groupedPosts = groupPostsByMonthYear(lisOfPost)
                Log.d("Postsss", "Posts fetched: $lisOfPost")
                Log.d("Postsss", "Grouped Posts fetched: $groupedPosts")
            },
            onFailure = {
                Log.e("ProfileScreen", "Error fetching posts")
            }
        )
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { index ->
                isRowVisible = index == 0
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            Column(
                modifier = Modifier
                    .padding(top = 40.dp)
                    .padding(horizontal = 20.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!isRowVisible) {
                            NavButton(
                                painter = null,
                                onClickAction = {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        listState.scrollToItem(0)
                                    }},
                                isChecked = true,
                                url = imgUrl,
                                name = name
                            )
                            Text(
                                text = name,
                                fontFamily = nunitoFontFamily,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                        }
                    }
                    Row {
                        NavButton(
                            painter = painterResource(R.drawable.ic_setting),
                            onClickAction = { navigateToSetting() }
                        )
                        BackArrow(
                            modifier = Modifier
                                .padding(0.dp)
                                .size(48.dp)
                                .rotate(180f)
                        )
                    }
                }
                if (isRowVisible) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp).padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                        ) {
                            if (imgUrl.isNotEmpty()) {
                                UserAvatar(imgUrl, 100.dp)
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
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        navigateToFollow("following", uid)
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

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        navigateToFollow("follower", uid)
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
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        groupedPosts.forEach() { (title, posts) ->
                            Log.e("GroupPostsss", "Size of GroupPost: ${groupedPosts.size}")
                            item(posts){
                                Log.e("GroupPostsss", "GroupPost: $title: $posts")
                                CalendarWithImages(
                                    posts = posts,
                                    title = title
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                    }
                }
            }
        }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CalendarWithImages(
    posts: List<Post>,
    title: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier.clip(RoundedCornerShape(15))
    ) {
        Column(
            modifier = modifier
                .background(MaterialTheme.colorScheme.secondary)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.tertiary)
                    .height(50.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 10.dp),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp
                )
            }
            FlowRow(
                modifier = Modifier
                    .padding(10.dp, 10.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                posts.forEach { post ->
                    Log.e("Post", "Post: $post")
                    PostImage(
                        url = post.photoUri,
                        modifier = Modifier
                            .size(60.dp)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(15))
                            .clickable {
                                val intent = Intent(context, PostActivity::class.java).apply {
                                    putExtra("POST_ID", post.postId)
                                }
                                context.startActivity(intent)
                            }
                    )
                }
            }
        }
    }
}

@Composable
fun UserAvatar(avatarUrl: String, size: Dp) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .size(size)
    ) {
        val secureUrl = remember(avatarUrl) {
            avatarUrl.replace("http://", "https://")
        }

        AsyncImage(
            model = secureUrl,
            contentDescription = "User avatar",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun UserWithOutAvatar(name: String, sizeText: TextUnit, sizeBox: Dp) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .size(sizeBox)
            .background(MaterialTheme.colorScheme.secondary)
    ) {
        Text(
            text = name.take(1).uppercase(),
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = sizeText,
            color = MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

fun groupPostsByMonthYear(posts: List<Post>): Map<String, List<Post>> {
    val outputDateFormat = SimpleDateFormat("MMMM, yyyy", Locale.getDefault())

    return posts.groupBy { post ->
        val date = post.date.toDate()
        outputDateFormat.format(date)
    }
}