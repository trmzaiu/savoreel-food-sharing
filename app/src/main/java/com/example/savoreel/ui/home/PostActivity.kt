package com.example.savoreel.ui.home

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.savoreel.R
import com.example.savoreel.model.NotificationViewModel
import com.example.savoreel.model.Post
import com.example.savoreel.model.PostModel
import com.example.savoreel.model.PostViewModel
import com.example.savoreel.model.ThemeViewModel
import com.example.savoreel.model.UserViewModel
import com.example.savoreel.ui.component.PostTopBar
import com.example.savoreel.ui.theme.SavoreelTheme
import com.example.savoreel.ui.theme.nunitoFontFamily

class PostActivity: ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        themeViewModel.loadUserSettings()

        setContent {
            val isDarkMode by themeViewModel.isDarkModeEnabled.observeAsState(initial = false)
            val postId = intent.getStringExtra("POST_ID") ?: ""
            Log.d("PostActivity", "Received POST_ID: $postId")

            SavoreelTheme(darkTheme = isDarkMode) {
                PostScreen(postId = postId)
            }
        }
    }
}

@Composable
fun PostScreen(postId: String){
    val userViewModel: UserViewModel = viewModel()
    val postModel: PostModel = viewModel()
    val postViewModel: PostViewModel = viewModel()
    val notificationViewModel: NotificationViewModel = viewModel()

    val currentUser by userViewModel.user.collectAsState()
    var name by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }

    var post by remember { mutableStateOf(Post()) }
    val context = LocalContext.current

    LaunchedEffect(currentUser) {
        userViewModel.getUser(
            onSuccess = { currentUser ->
                if (currentUser != null) {
                    name = currentUser.name.toString()
                    url = currentUser.avatarUrl.toString()
                } else {
                    Log.e("TakePhotoActivity", "User data not found")
                }
            },
            onFailure = { error ->
                Log.e("TakePhotoActivity", "Error retrieving user: $error")
            }
        )
    }

    LaunchedEffect(Unit) {
        postModel.getPost(
            postId = postId,
            onSuccess = {
                if (it != null) {
                    post = it
                }
            },
            onFailure = {  }
        )
    }

    Box(){
        Column(){
            PostTopBar(url,name)
            Column(modifier = Modifier.padding(5.dp, 130.dp, 5.dp, 15.dp)){
                Column(modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 30.dp)) {
                    Text(
                        text = post.name,
                        fontSize = 20.sp,
                        lineHeight = 20.sp,
                        fontFamily = nunitoFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(30.dp))
                                .background(Color.Gray)
                        ) {
                            val secureUrl = remember(post.photoUri) {
                                post.photoUri.replace("http://", "https://")
                            }

                            // Debug logging
                            LaunchedEffect(secureUrl) {
                                Log.d("ViewPostScreen", "Loading image from: $secureUrl")
                            }

                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(secureUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Post Photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                            )

                        }
                        if (post.title.isNotEmpty()) {
                            EditableField(
                                label = "Add Title",
                                value = post.title,
                                onStartEdit = { },
                                isTitle = true,
                            )
                        }
                        if (post.hashtag.isNotEmpty()) {
                            EditableField(
                                label = "Add Hashtag",
                                value = post.hashtag,
                                onStartEdit = {},
                                ic = R.drawable.ic_hashtag
                            )
                        }
                        if (post.location.isNotEmpty()) {
                            EditableField(
                                label = "Add Location",
                                value = post.location,
                                onStartEdit = {},
                                ic = R.drawable.ic_location
                            )
                        }
                    }
                }
            }
        }
    }
}