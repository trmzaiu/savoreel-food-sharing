package com.example.savoreel.ui.home

import RequestCameraPermission
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.savoreel.model.PostModel
import com.example.savoreel.model.ThemeViewModel
import com.example.savoreel.model.UserViewModel
import com.example.savoreel.ui.component.PostTopBar
import com.example.savoreel.ui.theme.SavoreelTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

class TakePhotoActivity : ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        themeViewModel.loadUserSettings()

        setContent {
            val isDarkMode by themeViewModel.isDarkModeEnabled.observeAsState(initial = false)

            SavoreelTheme(darkTheme = isDarkMode) {
                TakePhotoScreen()
            }
        }
    }
}

enum class PhotoState {
    None,
    TakePhoto,
    PhotoTaken,
    ViewPost
}
enum class SheetContent {
    NONE,
    EMOJI_PICKER,
    OPTIONS
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun TakePhotoScreen(
) {
    val userViewModel: UserViewModel = viewModel()
    val postViewModel: PostViewModel = viewModel()
    val postModel: PostModel = viewModel()

    val currentState by postViewModel.currentState
    var name by remember { mutableStateOf("") }

//    val permissionGranted by postViewModel.isPermissionGranted
    val photoUri by postViewModel.photoUri
    val editingField by postViewModel.editingField
    val title by postViewModel.title
    val location by postViewModel.location
    val hashtag by postViewModel.hashtag
    val options by postViewModel.option
    val isFrontCamera by postViewModel.isFrontCamera
    val flashEnabled by postViewModel.flashEnabled
    val currentSheetContent by postViewModel.currentSheetContent

    var selectedEmoji by remember { mutableStateOf<String?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val pagerState = rememberPagerState()

    val permissionGranted = remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    LaunchedEffect(currentState) {
        if (currentState == PhotoState.PhotoTaken) {
            pagerState.scrollToPage(0)
        }
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

    if (!permissionGranted.value) {
        RequestCameraPermission(
            onPermissionGranted = { permissionGranted.value = true },
            onPermissionDenied = { permissionGranted.value = false },
        )
    } else {
        Scaffold(
            topBar = { PostTopBar() },
            content = { padding ->
                if (currentState != PhotoState.PhotoTaken) {
                    VerticalPager(
                        count = 2,
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) { page ->
                        when (page) {
                            0 -> {
                                TakePhotoScreen(
                                    postViewModel,
                                    isFrontCamera,
                                    flashEnabled,
                                    name
                                )
                            }

                            1 -> {
                                ViewPostScreen(
                                    postViewModel,
                                    postModel,
                                    scope,
                                    sheetState,
                                    photoUri,
                                    title,
                                    location,
                                    hashtag,
                                    parentPagerState = pagerState
                                )
                            }
                        }
                    }
                } else {
//                    when (currentState) {
//                        PhotoState.TakePhoto -> TakePhotoScreen(
//                            postViewModel,
//                            isFrontCamera,
//                            flashEnabled,
//                            name
//                        )

                     PhotoTakenScreen(
                        padding,
                        postViewModel,
                        postModel,
                        scope,
                        sheetState,
                        photoUri,
                        title,
                        location,
                        hashtag,
                        editingField,
                        name
                    )

//                        PhotoState.ViewPost -> ViewPostScreen(
//                            postViewModel,
//                            postModel,
//                            scope,
//                            sheetState,
//                            photoUri,
//                            title,
//                            location,
//                            hashtag
//                        )
//
//                        else -> {}
//                    }
                }
            }
        )
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
                                selectedEmoji = emoji
//                        postViewModel.addEmojiToPost(emoji)
                                scope.launch { sheetState.hide() }
                            }
                        )
                    }

                SheetContent.OPTIONS -> BottomSheet(
                    scope = scope,
                    sheetState = sheetState,
                    postViewModel = postViewModel,
                    photoUri = photoUri,
                    context = context,
                    options = options
                )
                else -> {}
            }
        }
    }
    editingField?.let { field ->
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
    }
}
