package com.example.savoreel.ui.setting

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.savoreel.R
import com.example.savoreel.ui.component.BackArrow
import com.example.savoreel.ui.theme.SavoreelTheme
import com.example.savoreel.ui.theme.nunitoFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    var isDarkModeEnabled by rememberSaveable { mutableStateOf(false) }  // Add this state
    var showModal by remember { mutableStateOf(false) }
    val currentDarkMode = rememberUpdatedState(isDarkModeEnabled)
    var currentName by remember { mutableStateOf("") }

    SavoreelTheme(darkTheme = currentDarkMode.value) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                BackArrow(
                    modifier = Modifier.align(Alignment.CenterStart),
                    onClick = { navController.popBackStack() }
                )
                Text(
                    text = "Setting",
                    fontFamily = nunitoFontFamily,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 32.sp,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 40.dp)
                )
            }

            LazyColumn {
                item {
                    Spacer(modifier = Modifier.height(30.dp))
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.rounded_logo), // Replace with your actual icon
                            contentDescription = "User Avatar",
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable { showModal = true }
                                .size(150.dp),

                            contentScale = ContentScale.Crop
                        )
                    }

                    // Hiển thị ModalBottomSheet khi showModal = true
                    if (showModal) {
                        ModalBottomSheet(
                            onDismissRequest = { showModal = false }, // Đóng banner
                            sheetState = rememberModalBottomSheetState(
                                skipPartiallyExpanded = true
                            ),
                            containerColor = MaterialTheme.colorScheme.secondary // Màu nền cho sheet
                        )
                        {
                            SheetContent(
                                onOptionClick = { option ->
                                    showModal = false
                                    handleAvatarOption(option) // Xử lý khi chọn tùy chọn
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Name",
                        fontFamily = nunitoFontFamily,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.fillMaxWidth()

                    )
                    // General Section
                    Spacer(modifier = Modifier.height(20.dp))
                    SettingsSection(title = "General") {
                        SettingItemWithNavigation(
                            icon = Icons.Filled.Edit,
                            text = "Edit Name",
                            navController = navController,
                            destination = "name_screen/${currentName}"
                        )
                        SettingItemWithNavigation(
                            icon = Icons.Filled.Email,
                            text = "Change Email",
                            navController = navController,
                            destination = "email_screen?isChangeEmail=false"
                        )
                        SettingItemWithNavigation(
                            icon = Icons.Filled.Lock,
                            text = "Change Password",
                            navController = navController,
                            destination = "change_password_screen"
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Support Section
                    SettingsSection(title = "Support") {
                        SettingItemWithSwitch(
                            icon = Icons.Default.Build, // Moon icon for Dark Mode
                            text = "Dark Mode",
                            isChecked = isDarkModeEnabled,
                            onCheckedChange = { isDarkModeEnabled = it }
                        )
                        SettingItemWithNavigation(
                            icon = Icons.Filled.Place,
                            text = "Language",
                            navController = navController,
                            destination = "language"
                        )
                        SettingItemWithNavigation(
                            icon = Icons.Filled.Notifications,
                            text = "Notifications",
                            navController = navController,
                            destination = "notification_setting"
                        )
                        SettingItemWithNavigation(
                            icon = Icons.Filled.Warning,
                            text = "Report a problem",
                            navController = navController,
                            destination = "report_a_problem"
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // About Section
                    SettingsSection(title = "About") {
                        SettingItemWithNavigation(
                            icon = Icons.Filled.Share,
                            text = "Share Account",
                            navController = navController,
                            destination = "share_account"
                        )
                        SettingItemWithNavigation(
                            icon = Icons.Filled.Phone,
                            text = "Terms of Service",
                            navController = navController,
                            destination = "terms_of_service"
                        )
                        SettingItemWithNavigation(
                            icon = Icons.Filled.Lock,
                            text = "Privacy",
                            navController = navController,
                            destination = "privacy"
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    SettingsSection(title = "Danger Zone") {
                        SettingItemWithNavigation(
                            icon = Icons.Filled.Delete,
                            text = "Delete Account",
                            navController = navController,
                            destination = "confirm_password"
                        )
                        SettingItemWithNavigation(
                            icon = Icons.Filled.ArrowBack,
                            text = "Sign Out",
                            navController = navController,
                            destination = "sign_out"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = title,
            fontFamily = nunitoFontFamily,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier
                .padding(bottom = 4.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            content()
        }
    }
}

@Composable
fun SettingItemWithNavigation(
    icon: ImageVector,
    text: String,
    navController: NavController,
    destination: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .height(48.dp)
            .clickable { navController.navigate(destination) }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .padding(end = 16.dp)
                .align(Alignment.CenterVertically)
        )
        Text(
            text = text,
            fontSize = 20.sp,
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier
                .align(Alignment.CenterVertically)
        )
    }
}

@Composable
fun SettingItemWithSwitch(icon: ImageVector, text: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .height(48.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .padding(end = 16.dp)
                .align(Alignment.CenterVertically)
        )
        Text(
            text = text,
            fontSize = 20.sp,
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier
                .align(Alignment.CenterVertically)
        )
        Spacer(modifier = Modifier.weight(1f)) // Đẩy công tắc sang bên phải
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.secondary, // Màu khi bật
                uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                checkedTrackColor = MaterialTheme.colorScheme.primary,  // Màu track khi bật
                uncheckedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) // Mờ hơn cho track
            ),
            modifier = Modifier
                .align(Alignment.CenterVertically)
        )
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
                color = MaterialTheme.colorScheme.tertiary,

                )
        }
    }
}

// Hàm xử lý các tùy chọn từ Modal
fun handleAvatarOption(option: String) {
    when (option) {
        "Upload Image" -> {
            // Xử lý upload ảnh
        }
        "Take Photo" -> {
            // Xử lý chụp ảnh
        }
        "Remove Avatar" -> {
            // Xử lý xóa avatar
        }
        "Cancel" -> {
            // Không làm gì cả
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreviewDark() {
    SavoreelTheme(darkTheme = true, dynamicColor = true) {
        SettingsScreen(navController = rememberNavController()) // Pass the navController as normal
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SavoreelTheme(darkTheme = false, dynamicColor = false) {
        SettingsScreen(navController = rememberNavController()) // Pass the navController as normal
    }
}
