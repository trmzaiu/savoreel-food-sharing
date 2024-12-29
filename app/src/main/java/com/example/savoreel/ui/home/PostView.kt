//package com.example.savoreel.ui.home
//
//import RequestCameraPermission
//import android.Manifest
//import android.content.pm.PackageManager
//import android.util.Log
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.ModalBottomSheet
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.material3.rememberModalBottomSheetState
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.core.content.ContextCompat
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.NavController
//import com.example.savoreel.model.UserViewModel
//import com.example.savoreel.ui.component.PostTopBar
//import com.example.savoreel.ui.theme.secondaryDarkColor
//import kotlinx.coroutines.launch
//
////enum class PhotoState {
////    TakePhoto,
////    PhotoTaken,
////    ViewPost
////}
////enum class SheetContent {
////    NONE,
////    EMOJI_PICKER,
////    OPTIONS
////}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun PostView(
//    navController: NavController,
//    userViewModel: UserViewModel,
//    postViewModel: PostViewModel = viewModel()
//) {
//    val currentState by postViewModel.currentState
//    var name by remember { mutableStateOf("") }
//
////    val permissionGranted by postViewModel.isPermissionGranted
//    val photoUri by postViewModel.photoUri
//    val editingField by postViewModel.editingField
//    val title by postViewModel.title
//    val location by postViewModel.location
//    val hashtag by postViewModel.hashtag
//    val options by postViewModel.option
//    val isFrontCamera by postViewModel.isFrontCamera
//    val flashEnabled by postViewModel.flashEnabled
//    val currentSheetContent by postViewModel.currentSheetContent
//
//    var selectedEmoji by remember { mutableStateOf<String?>(null) }
//    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
//    val scope = rememberCoroutineScope()
//    val context = LocalContext.current
//
//    val permissionGranted = remember {
//        mutableStateOf(
//            ContextCompat.checkSelfPermission(
//                context,
//                Manifest.permission.CAMERA
//            ) == PackageManager.PERMISSION_GRANTED
//        )
//    }
//    LaunchedEffect(Unit) {
//        userViewModel.getUser(onSuccess = { user ->
//            if (user != null) {
//                name = user.name ?: ""
//            } else {
//                Log.e("NameTheme", "User data not found")
//            }
//        }, onFailure = { error ->
//            Log.e("NameTheme", "Error retrieving user: $error")
//        })
//    }
//
//    if (!permissionGranted.value) {
//        RequestCameraPermission(
//            onPermissionGranted = { permissionGranted.value = true },
//            onPermissionDenied = { permissionGranted.value = false },
//        )
//    } else {
//        Scaffold(
//            topBar = { PostTopBar() },
//            content = { padding ->
//                Column(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(padding)
//                        .padding(10.dp, 0.dp, 10.dp, 15.dp),
//                ) {
//                    Text(
//                        text = name,
//                        fontSize = 20.sp,
//                        lineHeight = 10.sp,
//                        fontWeight = FontWeight.Bold,
//                        color = secondaryDarkColor,
//                        textAlign = TextAlign.Center,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(top = 60.dp, bottom = 10.dp)
//                    )
//                    Column(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .weight(1f),
//                        verticalArrangement = Arrangement.SpaceBetween
//                    ) {
//                        when (currentState) {
//                            PhotoState.TakePhoto -> TakePhotoScreen(postViewModel, isFrontCamera, flashEnabled)
//                            PhotoState.PhotoTaken -> PhotoTakenScreen(postViewModel, scope, sheetState, photoUri, title, location, hashtag, editingField)
//                            PhotoState.ViewPost -> ViewPostScreen(postViewModel, scope, sheetState, photoUri, title, location, hashtag)
//                        }
//                    }
//                }
//            }
//        )
//    }
//    if (sheetState.isVisible) {
//        ModalBottomSheet(
//            onDismissRequest = {
//                postViewModel.setcurrentSheetContent(SheetContent.NONE)
//                scope.launch { sheetState.hide() }
//            },
//            sheetState = sheetState,
//            modifier =
//                when (currentSheetContent) {
//                    SheetContent.EMOJI_PICKER -> Modifier.height(400.dp)
//                    else -> Modifier
//                }
//        ) {
//            when (currentSheetContent) {
//                SheetContent.EMOJI_PICKER ->
//                    Box(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .verticalScroll(rememberScrollState()) // Enable scrolling
//                    ) {
//                        EmojiPickerDialog(
//                            onEmojiSelected = { emoji ->
//                                selectedEmoji = emoji
////                        postViewModel.addEmojiToPost(emoji)
//                                scope.launch { sheetState.hide() }
//                            }
//                        )
//                    }
//
//                SheetContent.OPTIONS -> BottomSheet(
//                        scope = scope,
//                        sheetState = sheetState,
//                        postViewModel = postViewModel,
//                        photoUri = photoUri,
//                        context = context,
//                        options = options
//                )
//                else -> {}
//            }
//        }
//    }
//    editingField?.let { field ->
//        BlurredInputOverlay(
//            label = when (field) {
//                "title" -> "Enter Title"
//                "location" -> "Enter Location"
//                "hashtag" -> "Add Hashtags (e.g., #Food)"
//                else -> ""
//            },
//            initialValue = when (field) {
//                "title" -> title
//                "location" -> location
//                "hashtag" -> hashtag
//                else -> ""
//            },
//            onValueChange = { newValue ->
//                if (field == "hashtag") {
//                    val filteredValue = newValue
//                        .split(" ")
//                        .filter { it.startsWith("#") }
//                        .joinToString(" ")
//                    postViewModel.updateFieldValue(field, filteredValue)
//                } else {
//                    postViewModel.updateFieldValue(field, newValue)
//                }
//            },
//            onDone = { postViewModel.stopEditingField() },
//            maxCharacters = when (field) {
//                "title" -> 28
//                "location" -> 60
//                "hashtag" -> 30
//                else -> 0
//            }
//        )
//    }
//}