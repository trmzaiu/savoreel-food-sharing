package com.example.savoreel.ui.home

import CameraFrame
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.savoreel.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun TakePhotoScreen(
    postViewModel: PostViewModel,
    isFrontCamera: Boolean,
    flashEnabled: Boolean
) {
    var takePhotoAction: (() -> Unit)? by remember { mutableStateOf(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        postViewModel.setPhotoUri(uri)
        postViewModel.navigateToState(PhotoState.PhotoTaken)
    }

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
                    .clickable {  postViewModel.setisFlashEnabled(!flashEnabled) },
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun PhotoTakenScreen(
    postViewModel: PostViewModel,
    scope: CoroutineScope,
    sheetState: SheetState,
    photoUri: Uri?,
    title:  String,
    location: String,
    hashtag: String,
    editingField: String?
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
//                postViewModel.uploadPost()
            postViewModel.navigateToState(PhotoState.ViewPost)
        }
        ImageButton(R.drawable.ic_edit, "Edit", 35.dp) {
            scope.launch { sheetState.show() }
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

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ViewPostScreen(
    postViewModel: PostViewModel,
    scope: CoroutineScope,
    sheetState: SheetState,
    photoUri: Uri?,
    title:  String,
    location: String,
    hashtag: String,
) {
    var emojiSheetState by remember { mutableStateOf(false) }
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
        if (title.isNotEmpty()) {
            EditableField(
                label = "Add Title",
                value = title,
                onStartEdit = { },
                isTitle = true,
            )
        }
        if (hashtag.isNotEmpty()) {
            EditableField(
                label = "Add Hashtag",
                value = hashtag,
                onStartEdit = {},
                ic = R.drawable.ic_hashtag
            )
        }
        if (location.isNotEmpty()) {
            EditableField(
                label = "Add Location",
                value = location,
                onStartEdit = {},
                ic = R.drawable.ic_location
            )
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(30.dp))
                .background(Color.Gray)
        ) {
            listOf("😀", "😍", "😂").forEach { emoji ->
                Text(
                    text = emoji,
                    fontSize = 24.sp,
                    modifier = Modifier.clickable {
//                    postViewModel.addEmojiToPost(emoji)
                    }
                )
            }
            IconButton(onClick = {
                scope.launch { sheetState.show() }
                emojiSheetState = true
            }) {
//                Icon(Icons.Default.MoreHoriz, contentDescription = "More Emojis")
            }
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 10.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ImageButton(R.drawable.ic_download, "Show Grid", 40.dp) {
        }
        ImageButton(R.drawable.ic_camera, "Back Home", 75.dp) {
            postViewModel.navigateToState(PhotoState.TakePhoto)
        }
        ImageButton(R.drawable.ic_edit, "More", 35.dp) {
            scope.launch { sheetState.show() }
        }
    }
    postViewModel.setOption(
        listOf(
            "Download" to R.drawable.ic_download,
            "Share" to R.drawable.ic_share2,
            "Report" to R.drawable.ic_report
        )
    )
    if (sheetState.isVisible && emojiSheetState == true) {
        ModalBottomSheet(
            onDismissRequest = { scope.launch { sheetState.hide() } },
            sheetState = sheetState
        ) {
//            LazyVerticalGrid(columns = GridCells.Fixed(5)) {
//                items(EmojiCompat.get().getEmojiList()) { emoji ->
//                    Text(
//                        text = emoji,
//                        fontSize = 24.sp,
//                        modifier = Modifier
//                            .padding(8.dp)
//                            .clickable {
////                                postViewModel.addEmojiToPost(emoji)
//                                scope.launch { sheetState.hide() }
//                            }
//                    )
//                }
//            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun BottomSheet(
    scope: CoroutineScope,
    sheetState: SheetState,
    postViewModel: PostViewModel,
    photoUri: Uri?,
    options: List<Pair<String, Int>>
){
    var currentEditOption by remember { mutableStateOf("") }
    val context = LocalContext.current

    ModalBottomSheet(
        onDismissRequest = { scope.launch { sheetState.hide() } },
        sheetState = sheetState
    ) {
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

