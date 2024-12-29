package com.example.savoreel.ui.home

import CameraFrame
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.emoji2.emojipicker.EmojiPickerView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.savoreel.R
import com.example.savoreel.model.PostModel
import com.example.savoreel.ui.theme.SavoreelTheme
import com.example.savoreel.ui.theme.secondaryDarkColor
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun TakePhotoScreen(
    postViewModel: PostViewModel,
    isFrontCamera: Boolean,
    flashEnabled: Boolean,
    name: String
) {
    var takePhotoAction: (() -> Unit)? by remember { mutableStateOf(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        postViewModel.setPhotoUri(uri)
        if(uri != null) {
            Log.e("PhotoTakenScreen", "Uri: $uri")
            postViewModel.navigateToState(PhotoState.PhotoTaken)
        }
    }
    Column(
        modifier = Modifier
            .padding(10.dp, 0.dp, 10.dp, 15.dp),
    ) {
        Text(
            text = name,
            fontSize = 20.sp,
            lineHeight = 10.sp,
            fontWeight = FontWeight.Bold,
            color = secondaryDarkColor,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 60.dp, bottom = 8.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(30.dp))
                        .background(Color.Gray)
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
                            .clickable { postViewModel.setisFlashEnabled(!flashEnabled) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_flash),
                            contentDescription = "Toggle Flash",
                            tint = Color.White.copy(alpha = if (flashEnabled) 1f else 0.3f),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 20.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ImageButton(R.drawable.ic_upload, "Upload", 40.dp) {
                    galleryLauncher.launch("image/*")
                }
                ImageButton(R.drawable.ic_camera, "Take Photo", 75.dp) {
                    takePhotoAction?.invoke()
                }
                ImageButton(R.drawable.ic_camflip, "Swap Camera", 35.dp) {
                    postViewModel.setisFrontCamera(!isFrontCamera)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun PhotoTakenScreen(
    padding : PaddingValues,
    postViewModel: PostViewModel,
    postModel: PostModel,
    scope: CoroutineScope,
    sheetState: SheetState,
    photoUri: Uri?,
    title:  String,
    location: String,
    hashtag: String,
    editingField: String?,
    name: String,
) {
    val context = LocalContext.current

    CallBackState(postViewModel)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(10.dp, 0.dp, 10.dp, 15.dp),
    ) {
        Text(
            text = name,
            fontSize = 20.sp,
            lineHeight = 10.sp,
            fontWeight = FontWeight.Bold,
            color = secondaryDarkColor,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 60.dp, bottom = 8.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 20.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ImageButton(R.drawable.ic_discard, "Discard", 40.dp) {
                    postViewModel.resetPhoto()
                    postViewModel.navigateToState(PhotoState.TakePhoto)
                }
                ImageButton(R.drawable.ic_send, "Post", 75.dp) {
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
                                },
                                onFailure = { errorMessage ->
                                    Log.e("PhotoTakenScreen", "Post upload failed: $errorMessage")
                                }
                            )
                        } else {
                            Log.e("PhotoTakenScreen", "Failed to convert URI to ByteArray.")
                        }
                    } else {
                        Log.e("PhotoTakenScreen", "Photo URI is invalid. Cannot upload post.")
                    }
                }
                    ImageButton(R.drawable.ic_edit, "Edit", 35.dp) {
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

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalMaterial3Api::class,
    ExperimentalPagerApi::class
)
@Composable
fun ViewPostScreen(
    postViewModel: PostViewModel,
    postModel: PostModel,
    scope: CoroutineScope,
    sheetState: SheetState,
    photoUri: Uri?,
    title:  String,
    location: String,
    hashtag: String,
    parentPagerState: PagerState
) {
    val postsListPagerState  = rememberPagerState()
    val posts by postModel.posts.observeAsState(emptyList())

    LaunchedEffect(Unit) {
        postModel.getPostsFromFirebase()
    }

    CallBackState(postViewModel)
        Column(
            modifier = Modifier
                .padding(10.dp, 0.dp, 10.dp, 15.dp),
        ) {
            VerticalPager(
                count = posts.size,
                state = postsListPagerState
            ) { page ->
                val post = posts[page]

                Text(
                    text = post.name,
                    fontSize = 20.sp,
                    lineHeight = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = secondaryDarkColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 60.dp, bottom = 8.dp)
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
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
                    if (post.title?.isNotEmpty() == true) {
                        EditableField(
                            label = "Add Title",
                            value = title,
                            onStartEdit = { },
                            isTitle = true,
                        )
                    }
                    if (post.hashtag?.isNotEmpty() == true) {
                        EditableField(
                            label = "Add Hashtag",
                            value = hashtag,
                            onStartEdit = {},
                            ic = R.drawable.ic_hashtag
                        )
                    }
                    if (post.location?.isNotEmpty() == true) {
                        EditableField(
                            label = "Add Location",
                            value = location,
                            onStartEdit = {},
                            ic = R.drawable.ic_location
                        )
                    }
                }
            }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(0.dp, 20.dp),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp, 0.dp, 10.dp, 10.dp)
                                .clip(RoundedCornerShape(30.dp))
                                .background(Color.LightGray),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                listOf("😀", "😍", "😂", "❤️", "🔥").forEach { emoji ->
                                    Text(
                                        text = emoji,
                                        fontSize = 25.sp,
                                        modifier = Modifier.clickable {
                                            //                    postViewModel.addEmojiToPost(emoji)
                                        }
                                    )
                                }
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_emoji),
                                    contentDescription = "Emoji Picker",
                                    modifier = Modifier.size(30.dp).clickable {
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
                            ImageButton(R.drawable.ic_download, "Show Grid", 40.dp) {
                            }
                            ImageButton(R.drawable.ic_camera, "Back Home", 75.dp) {
                                postViewModel.resetPhoto()
                                scope.launch {
                                    parentPagerState.animateScrollToPage(0)
                                }
    //                            postViewModel.navigateToState(PhotoState.TakePhoto)
                            }
                            ImageButton(R.drawable.ic_edit, "More", 35.dp) {
                                postViewModel.setcurrentSheetContent(SheetContent.OPTIONS)
                                scope.launch { sheetState.show() }
                            }
                        }
//            }
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
//    scope: CoroutineScope,
//    emojiSheetState: SheetState
) {
    AndroidView(
        factory = { context ->
            EmojiPickerView(context).apply{
                setOnEmojiPickedListener { emoji ->
                    onEmojiSelected(emoji.toString())
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
    postViewModel: PostViewModel,
    photoUri: Uri?,
    context: Context,
    options: List<Pair<String, Int>>
){
    var currentEditOption by remember { mutableStateOf("") }

//    ModalBottomSheet(
//        onDismissRequest = { scope.launch { sheetState.hide() } },
//        sheetState = sheetState
//    ) {
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
//    }
}

@Composable
fun ImageButton(
    @DrawableRes iconId: Int,
    description: String,
    size : Dp = 40.dp,
    onClick: () -> Unit,
) {
    Icon(
        painter = painterResource(iconId),
        contentDescription = description,
        modifier = Modifier
            .size(size)
            .clickable { onClick() },
        tint = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
fun CallBackState(
    postViewModel: PostViewModel
    ){
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

//    val backPressedCallback = rememberUpdatedState {
//        postViewModel.navigateToState(PhotoState.TakePhoto)
//    }
    backDispatcher?.addCallback(
        LocalLifecycleOwner.current,
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                postViewModel.navigateToState(PhotoState.TakePhoto)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SavoreelTheme() {
    }
}
