package com.example.savoreel.ui.home

import CameraFrame
import RequestCameraPermission
import android.util.Log
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.savoreel.R
import com.example.savoreel.model.UserViewModel
import com.example.savoreel.ui.component.PostTopBar
import com.example.savoreel.ui.theme.secondaryDarkColor
import kotlinx.coroutines.launch


@OptIn(ExperimentalGlideComposeApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PostView(
    navController : NavController,
    userViewModel: UserViewModel,
    postViewModel: PostViewModel = viewModel(),
) {
    val photoUri by postViewModel.photoUri
    val isPhotoTaken by postViewModel.isPhotoTaken
    var isFrontCamera by remember { mutableStateOf(false) }
    var permissionGranted by remember { mutableStateOf(false) }
    var permissionDenied by remember { mutableStateOf(false) }
    var takePhotoAction: (() -> Unit)? by remember { mutableStateOf(null) }
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var flashEnabled by remember { mutableStateOf(false) }
    var showFlashOverlay by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var currentEditOption by remember { mutableStateOf("") }

    val title by postViewModel.title
    val location by postViewModel.location
    val hashtag by postViewModel.hashtag
    val editingField by postViewModel.editingField
    var showLocationPicker by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        postViewModel.setPhotoUri(uri)
    }

    LaunchedEffect(Unit) {
        userViewModel.getUser(onSuccess = { user ->
            if (user != null) {
                name = user.name ?: ""
            } else {
                Log.e("NameTheme", "User data not found")
            }
        }, onFailure = { error ->
            Log.e("NameTheme", "Error retrieving user: $error")
        })
    }

    if (!permissionGranted) {
        RequestCameraPermission(
            onPermissionGranted = { permissionGranted = true },
            onPermissionDenied = { permissionDenied = true }
        )
    } else {
        Scaffold(
            topBar = { PostTopBar(navController) },
            content = { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(10.dp, 0.dp, 10.dp, 15.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(top = 60.dp)
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
                                .padding(10.dp)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(30.dp))
                                .background(Color.Gray)
                        ) {
                            if (isPhotoTaken && photoUri != null) {
                                Log.d("PostView", "isPhotoTaken: $isPhotoTaken, photoUri: $photoUri")
                                GlideImage(
                                    model = photoUri,
                                    contentDescription = "Captured Photo",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Log.d("PostView", "Photo displayed: $photoUri")
                            } else {
                                Log.d("PostView", "Camera live view active")
                                CameraFrame(
                                    modifier = Modifier.fillMaxSize(),
                                    isFrontCamera = isFrontCamera,
                                    onCapturePhoto = { uri ->
                                        Log.d("PostView", "Photo captured, updating state")
                                        postViewModel.setPhotoUri(uri)
                                        postViewModel.setisPhotoTaken(true)
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
                                        .clickable { flashEnabled = !flashEnabled },
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
                        if (isPhotoTaken) {
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
                    }

                    // Bottom Buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp, 20.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (isPhotoTaken) {
                            // State after photo taken
                            ImageButton(R.drawable.ic_discard, "Discard", 40.dp) {
                                postViewModel.resetPhoto()
                            }
                            ImageButton(R.drawable.ic_send, "Post", 75.dp) {

                            }
                            ImageButton(R.drawable.ic_edit, "Edit", 35.dp) {
                                scope.launch { sheetState.show() }
                            }
                        } else {
                            // State before photo taken
                            ImageButton(R.drawable.ic_upload, "Upload", 40.dp) {
                                galleryLauncher.launch("image/*")
                            }
                            ImageButton(R.drawable.ic_camera, "Take Photo", 75.dp) {
                                takePhotoAction?.invoke()
                            }
                            ImageButton(R.drawable.ic_camflip, "Swap Camera", 35.dp) {
                                isFrontCamera = !isFrontCamera
                            }
                        }
                    }
                }
            }
        )
    }
    if (sheetState.isVisible) {
        ModalBottomSheet(
            onDismissRequest = { scope.launch { sheetState.hide() } },
            sheetState = sheetState
        ) {
            EditOptionsOverlay(
                onDismiss = { scope.launch { sheetState.hide() } },
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
    if (showLocationPicker) {
        LocationPickerOverlay(
            onLocationSelected = { selectedAddress ->
                postViewModel.updateFieldValue("location", selectedAddress)
                showLocationPicker = false
            },
            onDismiss = { showLocationPicker = false }
        )
    }
    editingField?.let { field ->
//        if (editingField == "location") {
//            LocationPickerOverlay(
//                onLocationSelected = { selectedAddress ->
//                    postViewModel.updateFieldValue("location", selectedAddress)
//                    postViewModel.stopEditingField()
//                },
//                onDismiss = { postViewModel.stopEditingField() }
//            )
//        } else {
            BlurredInputOverlay(
                label = when (field) {
                    "title" -> "Enter Title"
                    "location" -> "Enter Location"
                    "hashtag" -> "Add Hashtags (e.g., #Food)"
                    else -> ""
                },
                initialValue = when (field) {
                    "title" -> title
                    "location" -> location
                    "hashtag" -> hashtag
                    else -> ""
                },
                onValueChange = { newValue ->
                    if (field == "hashtag") {
                        val filteredValue = newValue
                            .split(" ")
                            .filter { it.startsWith("#") }
                            .joinToString(" ")
                        postViewModel.updateFieldValue(field, filteredValue)
                    } else {
                        postViewModel.updateFieldValue(field, newValue)
                    }
                },
                onDone = { postViewModel.stopEditingField() },
                maxCharacters = when (field) {
                    "title" -> 28
                    "location" -> 60
                    "hashtag" -> 30
                    else -> 0
                }
            )
//        }
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

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    SavoreelTheme() {
//        PostView(rememberNavController(), UserViewModel(), userId = "7qu2YPf1ncZza45VHZnIiXzuOTy1")
//    }
//}