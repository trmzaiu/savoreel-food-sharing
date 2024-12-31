package com.example.savoreel.ui.home

import CameraFrame
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.emoji2.emojipicker.EmojiPickerView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.savoreel.R
import com.example.savoreel.model.NotificationViewModel
import com.example.savoreel.model.PostModel
import com.example.savoreel.model.PostViewModel
import com.example.savoreel.model.UserViewModel
import com.example.savoreel.ui.theme.nunitoFontFamily
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

@Composable
fun TakePhotoScreen() {
    val postViewModel: PostViewModel = viewModel()
    val userViewModel: UserViewModel = viewModel()

    val isFrontCamera by postViewModel.isFrontCamera.collectAsState()
    val flashEnabled by postViewModel.flashEnabled.collectAsState()
    val currentUser by userViewModel.user.collectAsState()

    var takePhotoAction: (() -> Unit)? by remember { mutableStateOf(null) }
    var name by remember { mutableStateOf("") }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        postViewModel.setPhotoUri(uri)
        if (uri != null) {
            Log.e("PhotoTakenScreen", "Uri: $uri")
            postViewModel.navigateToState(PhotoState.PhotoTaken)
        }
    }

    LaunchedEffect(currentUser) {
        userViewModel.getUser(
            onSuccess = { currentUser ->
                if (currentUser != null) {
                    name = currentUser.name.toString()
                } else {
                    Log.e("TakePhotoActivity", "User data not found")
                }
            },
            onFailure = { error ->
                Log.e("TakePhotoActivity", "Error retrieving user: $error")
            }
        )
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = name,
                fontSize = 20.sp,
                lineHeight = 20.sp,
                fontFamily = nunitoFontFamily,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 60.dp, bottom = 8.dp)
            )
            Box(modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(30.dp))
            ) {
                CameraFrame(
                    isFrontCamera = isFrontCamera,
                    onCapturePhoto = { uri ->
                        postViewModel.setPhotoUri(uri)
                        postViewModel.setisPhotoTaken(true)
                        postViewModel.navigateToState(PhotoState.PhotoTaken)
                    },
                    onTakePhoto = { action -> takePhotoAction = action },
                    flashEnabled = flashEnabled,
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f))
                        .clickable { postViewModel.setFlashEnabled(!flashEnabled) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_flash),
                        contentDescription = "Toggle Flash",
                        tint = MaterialTheme.colorScheme.onTertiary.copy(alpha = if (flashEnabled) 1f else 0.3f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
        Box(modifier = Modifier
            .align(Alignment.BottomEnd)
            .background(MaterialTheme.colorScheme.background)){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 20.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ImageButton(R.drawable.ic_upload, "Upload") {
                    galleryLauncher.launch("image/*")
                }
                ImageButtonWithBg(R.drawable.circle, "Take Photo", 70.dp, 0.dp) {
                    takePhotoAction?.invoke()
                }
                ImageButton(R.drawable.ic_camflip, "Swap Camera") {
                    postViewModel.setFrontCamera(!isFrontCamera)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun PhotoTakenScreen(scope: CoroutineScope,
    sheetState: SheetState,
    photoUri: Uri?,
    title:  String,
    location: String,
    hashtag: String,
    editingField: String?,
    postModel: PostModel
) {
    val userViewModel: UserViewModel = viewModel()
    val postViewModel: PostViewModel = viewModel()
    val notificationViewModel: NotificationViewModel = viewModel()
    val context = LocalContext.current

    val currentUser by userViewModel.user.collectAsState()

    var name by remember { mutableStateOf("") }
    var listOfFollowers by remember { mutableStateOf(emptyList<String>()) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(currentUser) {
        userViewModel.getUser(
            onSuccess = { currentUser ->
                if (currentUser != null) {
                    name = currentUser.name.toString()
                    listOfFollowers = currentUser.followers
                } else {
                    Log.e("TakePhotoActivity", "User data not found")
                }
            },
            onFailure = { error ->
                Log.e("TakePhotoActivity", "Error retrieving user: $error")
            }
        )
    }

    CallBackState()
    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = name,
                fontSize = 20.sp,
                lineHeight = 20.sp,
                fontFamily = nunitoFontFamily,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 60.dp, bottom = 8.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(30.dp))
                    .background(Color.Gray)
            ) {
                GlideImage(
                    model = photoUri,
                    contentDescription = "Captured Photo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.height(3.dp))
            EditableField(
                label = "Add Title",
                value = title,
                onStartEdit = { postViewModel.startEditingField("title") },
                isTitle = true,
            )
            if (hashtag.isNotEmpty() || editingField == "Hashtag") {
                EditableField(
                    label = "Add Hashtag",
                    value = hashtag,
                    onStartEdit = { postViewModel.startEditingField("hashtag") },
                    ic = R.drawable.ic_hashtag
                )
            }
            if (location.isNotEmpty() || editingField == "Location") {
                EditableField(
                    label = "Add Location",
                    value = location,
                    onStartEdit = { postViewModel.startEditingField("location") },
                    ic = R.drawable.ic_location
                )
            }

        }
        Box(modifier = Modifier.align(Alignment.BottomEnd)){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 20.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ImageButton(R.drawable.ic_discard, "Discard") {
                    postViewModel.resetPhoto()
                    postViewModel.navigateToState(PhotoState.TakePhoto)
                }
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.tertiary)
                            .size(75.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(40.dp),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                } else {
                    ImageButtonWithBg(R.drawable.ic_send, "Post", 40.dp, 5.dp) {
                        isLoading = true
                        if (photoUri != null && photoUri != Uri.EMPTY) {
                            val inputStream = context.contentResolver.openInputStream(photoUri)
                            val photoData = inputStream?.readBytes() ?: byteArrayOf()

                            if (photoData.isNotEmpty()) {
                                postModel.uploadPost(
                                    name = name,
                                    title = title,
                                    hashtag = hashtag,
                                    location = location,
                                    photoData = photoData,
                                    onSuccess = {
                                        postViewModel.navigateToState(PhotoState.TakePhoto)
                                        postViewModel.resetPhoto()
                                        isLoading = false
                                    },
                                    onFailure = { errorMessage ->
                                        Log.e("PhotoTakenScreen", "Post upload failed: $errorMessage")
                                        isLoading = false
                                    }
                                )
                                notificationViewModel.createNotifications(
                                    recipientIds = listOfFollowers,
                                    type = "Upload",
                                    message = "uploaded new photo",
                                    onSuccess = {},
                                    onFailure = {},
                                )
                            } else {
                                Log.e("PhotoTakenScreen", "Failed to convert URI to ByteArray.")
                                isLoading = false
                            }
                        } else {
                            Log.e("PhotoTakenScreen", "Photo URI is invalid. Cannot upload post.")
                            isLoading = false
                        }
                    }
                }
                ImageButton(R.drawable.ic_edit, "Edit") {
                    postViewModel.setcurrentSheetContent(SheetContent.OPTIONS)
                    scope.launch { sheetState.show() }
                }
            }
        }
    }
    postViewModel.setOption(
        listOf(
            "Hashtag" to R.drawable.ic_hashtag,
            "Location" to R.drawable.ic_location,
            "Download" to R.drawable.ic_download,
            "Share" to R.drawable.ic_share2
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun ViewPostScreen(
    scope: CoroutineScope,
    sheetState: SheetState,
    parentPagerState: PagerState,
    emojiList: MutableList<FloatingEmoji>,
    postModel: PostModel,
    postID: String
) {
    val postViewModel: PostViewModel = viewModel()
    val notificationViewModel: NotificationViewModel = viewModel()
    val innerPagerState = rememberPagerState()
    val posts by postModel.posts.collectAsState(emptyList())
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    var emo by remember {mutableStateOf("")}
    var status by remember {mutableStateOf(false)}

    LaunchedEffect(posts) {
        if (posts.isEmpty()) {
            postModel.getFollowingUserIds()
        }
        isLoading = false
    }

    fun parseDate(dateString: String): Date? {
        val format = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return try {
            format.parse(dateString)
        } catch (e: Exception) {
            Log.e("Date Parsing", "Failed to parse date: $dateString", e)
            null
        }
    }

    val sortedPosts = posts.sortedByDescending { post ->
        val postDate = parseDate(post.date)
        postDate ?: Date(0)
    }

    BackHandler {
        scope.launch {
            parentPagerState.animateScrollToPage(0)
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)) {
        if (sortedPosts.isNotEmpty()) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                VerticalPager(
                    count = sortedPosts.size,
                    state = innerPagerState,
                ) { page ->
                    val post = sortedPosts[page]

                    Column(modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 15.dp)) {
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
                                .padding(bottom = 4.dp)
                        )
                        val timeAgo = remember(post.date) {
                            postModel.getTimeAgo(post.date)
                        }
                        Text(
                            text = timeAgo,
                            fontSize = 16.sp,
                            fontFamily = nunitoFontFamily,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSecondary,
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
                                postViewModel.setPhotoUri(Uri.parse(secureUrl))

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
                            if (post.hashtag?.isNotEmpty() == true) {
                                val hashtagsList: List<String> = post.hashtag
                                val hashtags = hashtagsList.joinToString(" ")
                                EditableField(
                                    label = "Add Hashtag",
                                    value = hashtags,
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
                            if (status) {
                                notificationViewModel.createNotification(
                                    recipientId = post.userId,
                                    type = "React",
                                    message = "react $emo your photo",
                                    onSuccess = {
                                        status = false
                                    },
                                    onFailure = {},
                                )
                            }
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
                        val currentPage = innerPagerState.currentPage
                        val currentPostId = posts.getOrNull(currentPage)?.postId
                        if (currentPostId != null) {
                            postViewModel.setPostID(currentPostId)
                        }

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
                                        postId = postID,
                                        emoji = emoji,
                                        onSuccess = {
                                            Log.d("Add Emoji", "Add to DB success")
                                        },
                                        onFailure = { error ->
                                            Log.d("Add Emoji", "Add to DB not success")
                                        }
                                    )
                                    status = true
                                    emo = emoji
                                }
                            )
                        }
                        Icon(
                            painter = painterResource(id = R.drawable.ic_emoji),
                            contentDescription = "Emoji Picker",
                            modifier = Modifier
                                .size(30.dp)
                                .clickable {
                                    scope.launch { sheetState.show() }
                                    postViewModel.setcurrentSheetContent(SheetContent.EMOJI_PICKER)
                                }
                        )
                    }

                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ImageButton(R.drawable.ic_grid_image, "Show Grid", 40.dp) {
                        val intent = Intent(context, GridPostActivity::class.java).apply {
                            putExtra("USER_ID", "Everyone")
                        }
                        context.startActivity(intent)
                    }
                    ImageButton(R.drawable.circle, "Back Home",45.dp) {
                        postViewModel.resetPhoto()
                        scope.launch {
                            parentPagerState.animateScrollToPage(0)
                        }
                    }
                    ImageButton(R.drawable.ic_more, "More") {
                        postViewModel.setcurrentSheetContent(SheetContent.OPTIONS)
                        scope.launch { sheetState.show() }
                    }
                }
            }
        }
    }
    postViewModel.setOption(
        listOf(
            "Download" to R.drawable.ic_download,
            "Share" to R.drawable.ic_share2,
            "Report" to R.drawable.ic_report
        )
    )

}

@Composable
fun EmojiPickerDialog(
    onEmojiSelected: (String) -> Unit,
) {
    AndroidView(
        factory = { context ->
            EmojiPickerView(context).apply{
                setOnEmojiPickedListener { emoji ->
                    val emojiCharacter = emoji.emoji
                    onEmojiSelected(emojiCharacter)

                }
            }
        },
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun BottomSheet(
    scope: CoroutineScope,
    sheetState: SheetState,
    photoUri: Uri?,
    context: Context,
    options: List<Pair<String, Int>>
){
    val postViewModel: PostViewModel = viewModel()
    var currentEditOption by remember { mutableStateOf("") }

    EditOptionsOverlay(
        onDismiss = { scope.launch { sheetState.hide() } },
        options = options,
        onSelect = { option ->
            when (option.lowercase()) {
                "hashtag" -> {
                    currentEditOption = "hashtag"
                    postViewModel.startEditingField("hashtag")
                }
                "location" -> {
                    currentEditOption = "location"
                    postViewModel.startEditingField("location")
                }
                "download" -> {
                    photoUri?.let {
                        downloadPhoto(context, it)
                    }
                }
                "share" -> {
                    photoUri?.let {
                        sharePhoto(context, it)
                    }
                }
            }
            scope.launch { sheetState.hide() }
        }
    )
}

@Composable
fun ImageButton(
    @DrawableRes iconId: Int,
    description: String,
    size: Dp = 35.dp,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Icon(
        painter = painterResource(iconId),
        contentDescription = description,
        modifier = Modifier
            .size(size)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        tint = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
fun ImageButtonWithBg(
    @DrawableRes iconId: Int,
    description: String,
    size: Dp = 40.dp,
    padding: Dp = 0.dp,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.tertiary)
            .size(75.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
    ) {
        Icon(
            painter = painterResource(iconId),
            contentDescription = description,
            modifier = Modifier
                .size(size)
                .align(Alignment.Center)
                .padding(end = padding),
            tint = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun CallBackState(){
    val postViewModel: PostViewModel = viewModel()
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    backDispatcher?.addCallback(
        LocalLifecycleOwner.current,
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                postViewModel.navigateToState(PhotoState.TakePhoto)
            }
        }
    )
}