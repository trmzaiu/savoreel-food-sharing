package com.example.savoreel.ui.setting

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.*
import com.example.savoreel.R
import com.example.savoreel.ui.component.BackArrow
import com.example.savoreel.ui.theme.backgroundLightColor
import com.example.savoreel.ui.theme.disableButtonColor
import com.example.savoreel.ui.theme.nunitoFontFamily
import com.example.savoreel.ui.theme.primaryButtonColor
import com.example.savoreel.ui.theme.secondaryLightColor
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    var isDarkModeEnabled by remember { mutableStateOf(false) }
    var showModal by remember { mutableStateOf(false) } // Trạng thái mở/đóng modal

    AppTheme(darkTheme = isDarkModeEnabled) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundLightColor)
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
                                .clip(CircleShape) // Cắt ảnh thành hình tròng
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
                            )
                        ) {
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
                        modifier = Modifier.fillMaxWidth()
                    )
                    // General Section
                    Spacer(modifier = Modifier.height(20.dp))
                    SettingsSection(title = "General") {
                        SettingItemWithNavigation(
                            icon = Icons.Filled.Edit,
                            text = "Edit Name",
                            navController = navController,
                            destination = "edit_name"
                        )
                        SettingItemWithNavigation(
                            icon = Icons.Filled.Email,
                            text = "Change Email",
                            navController = navController,
                            destination = "change_email"
                        )
                        SettingItemWithNavigation(
                            icon = Icons.Filled.Lock,
                            text = "Change Password",
                            navController = navController,
                            destination = "change_password"
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
                            destination = "notifications"
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
            modifier = Modifier
                .padding(bottom = 4.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(secondaryLightColor, shape = RoundedCornerShape(20.dp))
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
            .clickable { navController.navigate(destination) } // Điều hướng đến trang khác
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
            fontWeight = FontWeight.SemiBold,
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
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .align(Alignment.CenterVertically)
        )
        Spacer(modifier = Modifier.weight(1f)) // Đẩy công tắc sang bên phải
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = secondaryLightColor,  // Màu khi bật
                uncheckedThumbColor = secondaryLightColor,  // Màu khi tắt
                checkedTrackColor = primaryButtonColor,  // Màu track khi bật
                uncheckedTrackColor = disableButtonColor  // Màu track khi tắt
            ),
            modifier = Modifier
                .align(Alignment.CenterVertically)
        )
    }
}

@Composable
fun NavHostSetup() {
    val navController = rememberNavController()
    AppTheme(darkTheme = false) {
        NavHost(navController = navController, startDestination = "settings_screen") {
            composable("settings_screen") {
                SettingsScreen(navController = navController)
            }
            // Các destination khác khi nhấn vào mục cài đặt
            composable("edit_name") { /* Hiển thị trang Edit Name */ }
            composable("change_email") { /* Hiển thị trang Change Email */ }
            composable("change_password") { /* Hiển thị trang Change Password */ }
            composable("language") { /* Hiển thị trang Language */ }
            composable("notifications") { /* Hiển thị trang Notifications */ }
            composable("share_account") { /* Hiển thị trang Share Account */ }
            composable("terms_of_service") { /* Hiển thị trang Terms of Service */ }
            composable("privacy") { /* Hiển thị trang Privacy */ }
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
                textAlign = TextAlign.Center
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

@Composable
fun AppTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme)
            MaterialTheme.colorScheme.copy(primary = Color.Black) else MaterialTheme.colorScheme.copy(primary = Color.White),
        content = content
    )
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(navController = rememberNavController())
}
