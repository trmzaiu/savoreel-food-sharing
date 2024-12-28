package com.example.savoreel.ui.setting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.savoreel.R
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
import com.example.savoreel.ui.theme.SavoreelTheme
import com.example.savoreel.ui.theme.nunitoFontFamily
import com.google.firebase.auth.FirebaseAuth

@Suppress("DEPRECATION")
class SettingActivity : ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        themeViewModel.loadUserSettings()

        setContent {
            val isDarkMode by themeViewModel.isDarkModeEnabled.observeAsState(initial = false)

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

    var showModal by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }
    var isSignOut by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }

    val isDarkModeEnabled by themeViewModel.isDarkModeEnabled.observeAsState(initial = false)
    val currentUser by userViewModel.user.collectAsState()

    LaunchedEffect(currentUser) {
        userViewModel.getUser(
            onSuccess = { currentUser -> name = currentUser?.name.toString() },
            onFailure = { error -> Log.e("NameTheme", "Error retrieving user: $error") }
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
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.background)
            ) {
                BackArrow(
                    modifier = Modifier.align(Alignment.TopStart).padding(start = 20.dp, top = 40.dp)
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
                    ) {
                        Image(
                            painter = painterResource(R.drawable.default_avatar),
                            contentDescription = "User Avatar",
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable { showModal = true }
                                .size(150.dp),
                            contentScale = ContentScale.Crop
                        )
                    }

                    if (showModal) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.scrim)
                        ) {
                            ModalBottomSheet(
                                onDismissRequest = { showModal = false },
                                sheetState = rememberModalBottomSheetState(
                                    skipPartiallyExpanded = true
                                ),
                                containerColor = MaterialTheme.colorScheme.secondary
                            ) {
                                SheetContent(
                                    onOptionClick = { option ->
                                        showModal = false
                                        handleAvatarOption(option)
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

                    // General Section
                    Spacer(modifier = Modifier.height(20.dp))

                    SettingsSection(title = "General", imageVector = ImageVector.vectorResource(R.drawable.ic_general)) {
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
                    SettingsSection(title = "Support", imageVector = ImageVector.vectorResource(R.drawable.ic_support)) {
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
                    SettingsSection(title = "About", imageVector = ImageVector.vectorResource(R.drawable.ic_about)) {
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

                    SettingsSection(title = "Danger Zone", imageVector = ImageVector.vectorResource(R.drawable.ic_danger)) {
                        SettingItemWithNavigation(
                            icon = ImageVector.vectorResource(R.drawable.ic_delete),
                            text = "Delete account",
                            onClick = {
                                errorMessage = "Are you want to delete your account? This action cannot be undone."
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
                .background(color = MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(20.dp))
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
            .padding(top = 8.dp, bottom = 20.dp)
    ) {
        val options = listOf("Upload Image", "Take Photo", "Remove Avatar", "Cancel")

        options.forEach { option ->
            Text(
                text = option,
                fontSize = 24.sp,
                fontFamily = nunitoFontFamily,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOptionClick(option) }
                    .padding(vertical = 8.dp),
                textAlign = TextAlign.Center,
                color = if (option == "Remove Avatar") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,

                )
        }
    }
}

fun handleAvatarOption(option: String) {
    when (option) {
        "Upload Image" -> {

        }
        "Take Photo" -> {

        }
        "Remove Avatar" -> {

        }
        "Cancel" -> {

        }
    }

}
