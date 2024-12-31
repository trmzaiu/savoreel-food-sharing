package com.example.savoreel.ui.setting

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.savoreel.R
import com.example.savoreel.model.PostViewModel
import com.example.savoreel.model.ThemeViewModel
import com.example.savoreel.model.UserViewModel
import com.example.savoreel.ui.component.BackArrow
import com.example.savoreel.ui.component.ConfirmDialog
import com.example.savoreel.ui.component.CustomSwitch
import com.example.savoreel.ui.component.ForwardArrow
import com.example.savoreel.ui.component.IconTheme
import com.example.savoreel.ui.onboarding.NameActivity
import com.example.savoreel.ui.onboarding.OnboardingActivity
import com.example.savoreel.ui.onboarding.PasswordActivity
import com.example.savoreel.ui.onboarding.SignInActivity
import com.example.savoreel.ui.profile.UserAvatar
import com.example.savoreel.ui.profile.UserWithOutAvatar
import com.example.savoreel.ui.theme.SavoreelTheme
import com.example.savoreel.ui.theme.nunitoFontFamily
import com.google.firebase.auth.FirebaseAuth
import kotlin.math.roundToInt

@Suppress("DEPRECATION")
class SettingActivity : ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        themeViewModel.loadUserSettings()

        setContent {
            val isDarkMode by themeViewModel.isDarkModeEnabled.collectAsState()

            LaunchedEffect(isDarkMode) {
                setResult(Activity.RESULT_OK)
            }

            SavoreelTheme(darkTheme = isDarkMode) {
                SettingTheme(
                    onDeleteAccountSuccess = {
                        val intent = Intent(this, OnboardingActivity::class.java)
                        startActivity(intent)
                        finish()
                    },
                    onSignOutSuccess = {
                        val intent = Intent(this, SignInActivity::class.java)
                        startActivity(intent)
                        finish()
                    },
                    onChangeName = {
                        val intent = Intent(this, NameActivity::class.java).apply {
                            putExtra("isChangeName", true)
                        }
                        startActivity(intent)
                    },
                    onChangeEmail = {
                        val intent = Intent(this, PasswordActivity::class.java).apply {
                            putExtra("isChangePassword", false)
                        }
                        startActivity(intent)
                    },
                    onChangePassword = {
                        val intent = Intent(this, PasswordActivity::class.java).apply {
                            putExtra("isChangePassword", true)
                        }
                        startActivity(intent)
                    },
                    onChangeNotification = {
                        val intent = Intent(this, NotiSettingActivity::class.java)
                        startActivity(intent)
                    },
                    navigateToTerm = {
                        val intent = Intent(this, TermsActivity::class.java)
                        startActivity(intent)
                    },
                    navigateToPolicy = {
                        val intent = Intent(this, PolicyActivity::class.java)
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
            onFailure = { error -> Log.e("SettingActivity", "Error retrieving user: $error") }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingTheme(
    onDeleteAccountSuccess: () -> Unit,
    onSignOutSuccess: () -> Unit,
    onChangeName: () -> Unit,
    onChangeEmail: () -> Unit,
    onChangePassword: () -> Unit,
    onChangeNotification: () -> Unit,
    navigateToTerm: () -> Unit,
    navigateToPolicy: () -> Unit
) {
    val themeViewModel: ThemeViewModel = viewModel()
    val userViewModel: UserViewModel = viewModel()
    val postViewModel: PostViewModel = viewModel()

    var uid by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var imgRes by remember { mutableStateOf("") }
    var showModal by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }
    var isSignOut by remember { mutableStateOf(false) }
    var show by remember { mutableStateOf("") }
    val isDarkModeEnabled by themeViewModel.isDarkModeEnabled.collectAsState()
    val currentUser by userViewModel.user.collectAsState()
    var isLoading by remember { mutableStateOf(true) }
    val isChosen by postViewModel.isChosen

    LaunchedEffect(currentUser) {
        userViewModel.getUser(
            onSuccess = { currentUser ->
                name = currentUser?.name.toString()
                uid = currentUser?.userId.toString()
                imgRes = currentUser?.avatarUrl.toString()
                isLoading = false
            },
            onFailure = { error ->
                Log.e("NameTheme", "Error retrieving user: $error")
                isLoading = false
            }
        )
    }

    fun deleteAccount() {
        userViewModel.deleteUserAndData(
            onSuccess = {
                onDeleteAccountSuccess()
                Log.d("FirebaseAuth", "User account deleted successfully.")
            },
            onFailure = { error ->
                Log.e("FirebaseAuth", error)
            }
        )
    }

    fun signOut() {
        onSignOutSuccess()
        FirebaseAuth.getInstance().signOut()
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
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = MaterialTheme.colorScheme.background)
                ) {
                    BackArrow(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(start = 20.dp, top = 40.dp)
                    )

                    Text(
                        text = "Setting",
                        fontFamily = nunitoFontFamily,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 32.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(top = 40.dp, bottom = 10.dp)
                    )
                }

                LazyColumn(
                    modifier = Modifier.padding(horizontal = 35.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(20.dp))

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { showModal = true }
                        ) {
                            if (imgRes.isNotEmpty()) {
                                UserAvatar(imgRes, 150.dp)
                            } else {
                                UserWithOutAvatar(name, 100.sp, 150.dp)
                            }
                        }

                        if (showModal) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.scrim)
                            ) {
                                ModalBottomSheet(
                                    onDismissRequest = { showModal = false },
                                    sheetState = rememberModalBottomSheetState(
                                        skipPartiallyExpanded = true
                                    ),
                                    containerColor = MaterialTheme.colorScheme.secondary.copy(0.95f),
                                ) {
                                    SheetContent(
                                        onOptionClick = { option ->
                                            showModal = false
                                            show = option
                                            postViewModel.setIsChosen(true)
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(15.dp))

                        Text(
                            text = name,
                            fontFamily = nunitoFontFamily,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        SettingsSection(
                            title = "General",
                            imageVector = ImageVector.vectorResource(R.drawable.ic_general)
                        ) {
                            SettingItemWithNavigation(
                                icon = ImageVector.vectorResource(R.drawable.ic_name),
                                text = "Edit name",
                                onClick = { onChangeName() }
                            )
                            SettingItemWithNavigation(
                                icon = ImageVector.vectorResource(R.drawable.ic_mail),
                                text = "Change email",
                                onClick = { onChangeEmail() }
                            )
                            SettingItemWithNavigation(
                                icon = ImageVector.vectorResource(R.drawable.ic_key),
                                text = "Change password",
                                onClick = { onChangePassword() }
                            )
                        }

                        // Support Section
                        SettingsSection(
                            title = "Support",
                            imageVector = ImageVector.vectorResource(R.drawable.ic_support)
                        ) {
                            SettingItemWithNavigation(
                                icon = ImageVector.vectorResource(R.drawable.ic_darkmode),
                                text = "Dark mode",
                                changeMode = true,
                                isChecked = isDarkModeEnabled,
                                onCheckedChange = { themeViewModel.toggleDarkMode() }
                            )

                            SettingItemWithNavigation(
                                icon = ImageVector.vectorResource(R.drawable.ic_language),
                                text = "Language",
                                onClick = {}
                            )
                            SettingItemWithNavigation(
                                icon = ImageVector.vectorResource(R.drawable.ic_noti),
                                text = "Notifications",
                                onClick = { onChangeNotification() }
                            )
                            SettingItemWithNavigation(
                                icon = ImageVector.vectorResource(R.drawable.ic_report),
                                text = "Report a problem",
                                onClick = {}
                            )
                        }

                        // About Section
                        SettingsSection(
                            title = "About",
                            imageVector = ImageVector.vectorResource(R.drawable.ic_about)
                        ) {
                            SettingItemWithNavigation(
                                icon = ImageVector.vectorResource(R.drawable.ic_share),
                                text = "Share account",
                                onClick = {}
                            )
                            SettingItemWithNavigation(
                                icon = ImageVector.vectorResource(R.drawable.ic_term),
                                text = "Terms and conditions",
                                onClick = { navigateToTerm() }
                            )
                            SettingItemWithNavigation(
                                icon = ImageVector.vectorResource(R.drawable.ic_privacy),
                                text = "Policy privacy",
                                onClick = { navigateToPolicy() }
                            )
                        }

                        SettingsSection(
                            title = "Danger Zone",
                            imageVector = ImageVector.vectorResource(R.drawable.ic_danger)
                        ) {
                            SettingItemWithNavigation(
                                icon = ImageVector.vectorResource(R.drawable.ic_delete),
                                text = "Delete account",
                                onClick = {
                                    errorMessage =
                                        "Are you want to delete your account? This action cannot be undone."
                                    showErrorDialog = true
                                    isSignOut = false
                                }
                            )
                            SettingItemWithNavigation(
                                icon = ImageVector.vectorResource(R.drawable.ic_logout),
                                text = "Sign out",
                                onClick = {
                                    errorMessage = "Are you sure you want to sign out?"
                                    showErrorDialog = true
                                    isSignOut = true
                                }
                            )
                        }
                    }
                }
            }
        }

    }

    if (showErrorDialog) {
        ConfirmDialog(
            title = if (isSignOut) "Confirm" else "We're sorry to see you go!",
            message = errorMessage,
            onDismiss = {
                showErrorDialog = false
            },
            onConfirm = {
                if (isSignOut) {
                    showErrorDialog = false
                    signOut()
                } else {
                    showErrorDialog = false
                    deleteAccount()
                }
            }
        )
    }
    if (isChosen){
        HandleAvatarOption(show, postViewModel)
    }
}

@Composable
fun SettingsSection(title: String, imageVector: ImageVector? = null, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (imageVector != null) {
                Icon(
                    imageVector = imageVector,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier
                        .size(18.dp)
                )
            }
            Text(
                text = title,
                fontFamily = nunitoFontFamily,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSecondary,
            )
        }
        Column(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            content()
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun SettingItemWithNavigation(
    icon: ImageVector? = null,
    text: String,
    changeMode: Boolean = false,
    isChecked: Boolean = false,
    onCheckedChange: (Boolean) -> Unit = {},
    onClick: () -> Unit = {}
) {
    val modifier = if (!changeMode) {
        Modifier
            .fillMaxWidth()
            .height(55.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onClick()
            }
    } else {
        Modifier
            .fillMaxWidth()
            .height(55.dp)
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ){
        if (icon != null) {
            IconTheme(
                imageVector = icon,
            )
            Spacer(modifier = Modifier.width(16.dp))
        }

        Text(
            text = text,
            modifier = Modifier.weight(1f),
            fontSize = 20.sp,
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.Bold,
            color = if (text == "Delete account") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
        )

        if (changeMode) {
            CustomSwitch(
                checked = isChecked,
                onCheckedChange = onCheckedChange
            )
        } else {
            ForwardArrow()
        }
    }
}

@Composable
fun SheetContent(onOptionClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp)
    ) {
        val options = listOf("Upload Image", "Take Photo", "Remove Avatar", "Cancel")

        options.forEach { option ->
            Text(
                text = option,
                fontSize = 16.sp,
                fontFamily = nunitoFontFamily,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOptionClick(option) }
                    .padding(vertical = 5.dp),
                textAlign = TextAlign.Center,
                color = if (option == "Remove Avatar") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSecondary,
            )
        }
    }
}

@Composable
fun HandleAvatarOption(option: String, postViewModel: PostViewModel) {
    val userViewModel: UserViewModel = viewModel()
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var shouldLaunchGallery by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            Log.d("Gallery", "Image selected: $uri")
        } else {
            Log.d("Gallery", "No image selected")
        }
        shouldLaunchGallery = false
        postViewModel.setIsChosen(false)
    }

    LaunchedEffect(option) {
        if (option == "Upload Image" && !shouldLaunchGallery) {
            shouldLaunchGallery = true
            galleryLauncher.launch("image/*")
        }
    }

    when (option) {
        "Remove Avatar" -> {
            userViewModel.removeUserAvatar(
                avatarUrl = "",
                onSuccess = {
                    Log.d("Avatar", "Successful")
                },
                onFailure = { error ->
                    Log.e("Avatar", "Error: $error")
                }
            )
            postViewModel.setIsChosen(false)
        }
        else -> { /* Handle other cases */ }
    }

    selectedImageUri?.let { uri ->
        val photoData = context.contentResolver.openInputStream(uri)?.readBytes() ?: byteArrayOf()
        Box (
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.9f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AdjustableImagePreview(imageUri = uri)
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            if (photoData.isNotEmpty()) {
                                userViewModel.uploadUserAvatar(
                                    photoData = photoData,
                                    onSuccess = {
                                        Log.d("Avatar", "Avatar uploaded successfully")
                                        selectedImageUri = null
                                    },
                                    onFailure = { error ->
                                        Log.e("Avatar", "Failed to upload avatar: $error")
                                    }
                                )
                            } else {
                                Log.e("Avatar", "Failed to convert URI to ByteArray.")
                            }

                            Log.d("Avatar", "Confirmed image: $uri")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = "Confirm",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontFamily = nunitoFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                    Button(
                        onClick = {
                            selectedImageUri = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Gray
                        )
                    ) {
                        Text(
                            text = "Cancel",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontFamily = nunitoFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdjustableImagePreview(imageUri: Uri) {
    val context = LocalContext.current
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset(0f, 0f)) }
    var imageDimensions by remember { mutableStateOf(Size(0f, 0f)) }

    // To calculate aspect ratio (width / height)
    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        scale *= zoomChange
        offset += panChange
    }

    // Fetch the image dimensions to determine the aspect ratio
    LaunchedEffect(imageUri) {

        val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(imageUri))
        imageDimensions = Size(bitmap.width.toFloat(), bitmap.height.toFloat())
    }

    Box(
        modifier = Modifier
            .size(400.dp) // Size of the circular image container
            .clip(CircleShape) // Ensure the image is clipped to a circle
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(onDoubleTap = {
                        // Reset scale and offset on double-tap
                        scale = 1f
                        offset = Offset(0f, 0f)
                    })
                }
        ) {
            AsyncImage(
                model = imageUri,
                contentDescription = "Adjustable Image",
                modifier = Modifier
                    .fillMaxSize()
                    .offset {
                        val maxOffsetX = (scale - 1f) * 200f // Maximum offset based on scale
                        val maxOffsetY = (scale - 1f) * 200f

                        // Adjust offset based on aspect ratio
                        val isLandscape = imageDimensions.width > imageDimensions.height
                        val isPortrait = imageDimensions.width < imageDimensions.height

                        val newOffsetX = if (isLandscape) {
                            // Only allow horizontal movement for landscape images
                            offset.x.coerceIn(-maxOffsetX, maxOffsetX)
                        } else {
                            0f // Disable horizontal movement for portrait images
                        }

                        val newOffsetY = if (isPortrait) {
                            // Only allow vertical movement for portrait images
                            offset.y.coerceIn(-maxOffsetY, maxOffsetY)
                        } else {
                            0f // Disable vertical movement for landscape images
                        }

                        IntOffset(newOffsetX.roundToInt(), newOffsetY.roundToInt())
                    }
                    .graphicsLayer(
                        scaleX = scale.coerceIn(1f, 3f), // Limit zoom from 1x to 3x
                        scaleY = scale.coerceIn(1f, 3f)
                    )
                    .transformable(transformableState),
                contentScale = ContentScale.Crop // Ensure image is cropped inside the circle
            )
        }
    }
}