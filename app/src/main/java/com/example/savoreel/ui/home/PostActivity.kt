package com.example.savoreel.ui.home

import android.app.Activity
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.savoreel.R
import com.example.savoreel.model.NotificationViewModel
import com.example.savoreel.model.Post
import com.example.savoreel.model.PostModel
import com.example.savoreel.model.PostViewModel
import com.example.savoreel.model.ThemeViewModel
import com.example.savoreel.model.UserViewModel
import com.example.savoreel.model.formatRelativeTime
import com.example.savoreel.ui.component.PostTopBar
import com.example.savoreel.ui.profile.ProfileActivity
import com.example.savoreel.ui.theme.SavoreelTheme
import com.example.savoreel.ui.theme.nunitoFontFamily
import kotlinx.coroutines.launch
import kotlin.random.Random


class PostActivity: ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        themeViewModel.loadUserSettings()

        setContent {
            val isDarkMode by themeViewModel.isDarkModeEnabled.collectAsState()
            val postId = intent.getStringExtra("POST_ID") ?: ""
            Log.d("PostActivity", "Received POST_ID: $postId")

            SavoreelTheme(darkTheme = isDarkMode) {
                PostScreen(postId = postId,
                    navigateToProfile = {
                        val intent = Intent(this, ProfileActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        finish()
                    },
                    navigateToSearch = {
                        val intent = Intent(this, SearchActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        finish()
                    },
                    navigateToNoti = {
                        val intent = Intent(this, NotificationActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun PostScreen(postId: String, navigateToProfile: () -> Unit, navigateToSearch: () -> Unit, navigateToNoti: () -> Unit){
    val userViewModel: UserViewModel = viewModel()
    val postModel: PostModel = viewModel()
    val postViewModel: PostViewModel = viewModel()
    val notificationViewModel: NotificationViewModel = viewModel()

    val currentUser by userViewModel.user.collectAsState()
    var name by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }

    var post by remember { mutableStateOf(Post()) }
    val context = LocalContext.current
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    val currentSheetContent by postViewModel.currentSheetContent
    val emojiList = remember { mutableStateListOf<FloatingEmoji>() }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(true) }

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
                isLoading = false
            },
            onFailure = { isLoading = false }
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)){
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            PostTopBar(url,name, navigateToProfile, navigateToSearch, navigateToNoti)
            Box(modifier = Modifier.padding(5.dp, 130.dp, 5.dp, 15.dp)) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(top = 30.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .align(Alignment.CenterHorizontally),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        var postName by remember { mutableStateOf("") }
                        userViewModel.getUserById(
                            post.userId,
                            onSuccess = { user -> postName = user?.name ?: "" },
                            onFailure = {}
                        )
                        Text(
                            text = postName,
                            fontSize = 20.sp,
                            fontFamily = nunitoFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        Text(
                            text = formatRelativeTime(post.date),
                            fontSize = 20.sp,
                            fontFamily = nunitoFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSecondary,
                        )
                    }
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(30.dp))
                                .background(Color.Gray)
                        ) {
                            photoUri = remember(post.photoUri) {
                                Uri.parse(post.photoUri.replace("http://", "https://"))
                            }
                            GlideImage(
                                model = photoUri,
                                contentDescription = "Captured Photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
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
                        if (post.hashtag?.isNotEmpty() == true) {
                            val hashtagsList: List<String> = post.hashtag!!
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_hashtag),
                                    contentDescription = "Add Hashtag",
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.onSecondary
                                )
                                Spacer(modifier = Modifier.width(15.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp),modifier = Modifier.weight(1f)){
                                    hashtagsList.forEach { hashtag ->
                                        Box(
                                            modifier = Modifier.clickable (
                                                interactionSource = remember { MutableInteractionSource() },
                                                indication = null
                                            ) {
                                                val intent = Intent(context, GridPostActivity::class.java).apply {
                                                    putExtra("HASH_TAG", hashtag)
                                                }
                                                context.startActivity(intent)
                                            }
                                        ) {
                                            Text(
                                                text = hashtag,
                                                color = MaterialTheme.colorScheme.onBackground,
                                                fontWeight = FontWeight.Normal,
                                                fontSize = 18.sp,
                                            )
                                        }
                                    }
                                }
                            }
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
        Box(modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(bottom = 20.dp)){
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp, 0.dp, 10.dp, 10.dp)
                        .clip(RoundedCornerShape(30.dp))
                        .background(MaterialTheme.colorScheme.tertiary),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        var status by remember {mutableStateOf(false)}
                        listOf("😀", "😍", "😂", "❤️", "🔥").forEach { emoji ->
                            Text(
                                text = emoji,
                                fontSize = 25.sp,
                                modifier = Modifier.clickable {
                                    repeat(20){
                                        val randomX = Random.nextFloat() * 350f
                                        val randomY = 1200f
                                        emojiList.add(FloatingEmoji(emoji, randomX, randomY))
                                    }
                                    postModel.uploadEmojiReaction(
                                        postId = postId,
                                        emoji = emoji,
                                        onSuccess = {
                                            Log.d("Add Emoji", "Add to DB success")
                                        },
                                        onFailure = { error ->
                                            Log.d("Add Emoji", "Add to DB not success")
                                        }
                                    )
                                    status = true
                                }
                            )
                            var currentName by remember {mutableStateOf("")}
                            userViewModel.getUser({
                                if (it != null) {
                                    currentName = it.name.toString()
                                }
                            }, {})
                            if (status) {
                                notificationViewModel.createNotification(
                                    recipientId = post.userId,
                                    postId = post.postId,
                                    type = "React",
                                    message = "react $emoji your photo",
                                    onSuccess = {
                                        status = false
                                    },
                                    onFailure = {},
                                )
                            }
                        }
                        ImageButton(R.drawable.ic_emoji, "More Emoji", 30.dp){
                            scope.launch { sheetState.show() }
                            postViewModel.setcurrentSheetContent(SheetContent.EMOJI_PICKER)
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ImageButton(R.drawable.ic_grid_image, "Show Grid", 40.dp) {
                        (context as? Activity)?.finish()
                    }
                    ImageButton(R.drawable.circle, "Back Home",45.dp) {
                        val intent = Intent(context, TakePhotoActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        context.startActivity(intent)
                    }
                    ImageButton(R.drawable.ic_more, "More") {
                        postViewModel.setcurrentSheetContent(SheetContent.OPTIONS)
                        scope.launch { sheetState.show() }
                    }
                }
            }

        }
        if (sheetState.isVisible) {
            ModalBottomSheet(
                onDismissRequest = {
                    postViewModel.setcurrentSheetContent(SheetContent.NONE)
                    scope.launch { sheetState.hide() }
                },
                sheetState = sheetState,
                modifier =
                when (currentSheetContent) {
                    SheetContent.EMOJI_PICKER -> Modifier.height(400.dp)
                    else -> Modifier
                }
            ) {
                when (currentSheetContent) {
                    SheetContent.EMOJI_PICKER ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        ) {
                            EmojiPickerDialog(
                                onEmojiSelected = { emoji ->
                                    repeat(20) {
                                        val randomX = Random.nextFloat() * 300f
                                        val randomY = Random.nextFloat() * 1000f
                                        emojiList.add(FloatingEmoji(emoji, randomX, randomY))
                                    }
                                    postModel.uploadEmojiReaction(
                                        postId = postId,
                                        emoji = emoji,
                                        onSuccess = {
                                            Log.d("Add Emoji", "Add to DB success")
                                        },
                                        onFailure = {
                                            Log.d("Add Emoji", "Add to DB not success")
                                        }
                                    )
                                    scope.launch { sheetState.hide() }
                                },
                            )
                        }

                    SheetContent.OPTIONS -> BottomSheet(
                        scope = scope,
                        sheetState = sheetState,
                        photoUri = photoUri,
                        context = context,
                        options = listOf(
                            "Download" to R.drawable.ic_download,
                            "Share" to R.drawable.ic_share2,
                            "Report" to R.drawable.ic_report
                        )
                    )
                    else -> {}
                }
            }
        }
    }
    EmojiAnimationDisplay(
        emojiList = emojiList,
        onAnimationEnd = { emoji ->
            emojiList.remove(emoji)
        }
    )
}