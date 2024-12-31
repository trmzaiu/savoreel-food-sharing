package com.example.savoreel.ui.home

import RequestCameraPermission
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
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
import com.example.savoreel.model.PostViewModel
import com.example.savoreel.model.ThemeViewModel
import com.example.savoreel.model.UserViewModel
import com.example.savoreel.ui.component.PostTopBar
import com.example.savoreel.ui.theme.SavoreelTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import kotlin.random.Random

class TakePhotoActivity : ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        themeViewModel.loadUserSettings()

        setContent {
            val isDarkMode by themeViewModel.isDarkModeEnabled.observeAsState(initial = false)

            SavoreelTheme(darkTheme = isDarkMode) {
                HomeScreen()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        userViewModel.getUser(
            onSuccess = { user -> userViewModel.setUser(user) },
            onFailure = { error -> Log.e("TakePhotoActivity", "Error retrieving user: $error") }
        )
    }
}

enum class PhotoState {
    None,
    TakePhoto,
    PhotoTaken,
}
enum class SheetContent {
    NONE,
    EMOJI_PICKER,
    OPTIONS
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun HomeScreen() {
    val userViewModel: UserViewModel = viewModel()
    val postViewModel: PostViewModel = viewModel()
    val postModel: PostModel = viewModel()

    val currentUser by userViewModel.user.collectAsState()

    val currentState by postViewModel.currentState

    var name by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }

    val photoUri by postViewModel.photoUri
    val editingField by postViewModel.editingField
    val title by postViewModel.title
    val location by postViewModel.location
    val hashtag by postViewModel.hashtag
    val options by postViewModel.option
    val postID by postViewModel.postID

    val currentSheetContent by postViewModel.currentSheetContent

    var selectedEmoji by remember { mutableStateOf<String?>("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val outerPagerState = rememberPagerState()
    val emojiList = remember { mutableStateListOf<FloatingEmoji>() }

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
            outerPagerState.scrollToPage(0)
        }
    }

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

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        if (!permissionGranted.value) {
            RequestCameraPermission(
                onPermissionGranted = { permissionGranted.value = true },
                onPermissionDenied = { permissionGranted.value = true }
            )
        } else {
            PostTopBar(url, name)
            Column(modifier = Modifier.padding(5.dp, 130.dp, 5.dp, 15.dp)) {
                if (currentState != PhotoState.PhotoTaken) {
                    VerticalPager(
                        count = 2,
                        state = outerPagerState,
                        modifier = Modifier.fillMaxSize().padding()
                    ) { outerPage ->
                        when (outerPage) {
                            0 -> {
                                TakePhotoScreen()
                            }

                            1 -> {
                                ViewPostScreen(
                                    scope,
                                    sheetState,
                                    outerPagerState,
                                    emojiList,
                                    postID
                                )
                            }
                        }
                    }
                } else {
                    PhotoTakenScreen(
                        scope,
                        sheetState,
                        photoUri,
                        title,
                        location,
                        hashtag,
                        editingField,
                        postModel
                    )
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
                                            postId = postID,
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
    }
    EmojiAnimationDisplay(
        emojiList = emojiList,
        onAnimationEnd = { emoji ->
            emojiList.remove(emoji)
        }
    )
}
