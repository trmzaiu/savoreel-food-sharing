package com.example.savoreel.ui.setting

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.*
import com.example.savoreel.R
import com.example.savoreel.ui.component.BackArrow
import com.example.savoreel.ui.theme.nunitoFontFamily
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.vectorResource
import com.example.savoreel.ui.theme.SavoreelTheme
import com.example.savoreel.ui.component.SettingItemWithSwitch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    var isDarkModeEnabled by rememberSaveable { mutableStateOf(false) }  // Add this state
    var showModal by remember { mutableStateOf(false) }
    val currentDarkMode = rememberUpdatedState(isDarkModeEnabled)

    SavoreelTheme(darkTheme = currentDarkMode.value) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
                .padding(horizontal = 20.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
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
                                .clip(CircleShape) // Cắt ảnh thành hình tròn
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
                            icon = ImageVector.vectorResource(id = R.drawable.pen),
                            text = "Edit Name",
                            navController = navController,
                            destination = "name_screen"
                        )
                        SettingItemWithNavigation(
                            icon = ImageVector.vectorResource(id = R.drawable.envelope),
                            text = "Change Email",
                            navController = navController,
                            destination = "email_screen"
                        )
                        SettingItemWithNavigation(
                            icon = ImageVector.vectorResource(id = R.drawable.key),
                            text = "Change Password",
                            navController = navController,
                            destination = "change_password_screen"
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    // Support Section
                    SettingsSection(title = "Support") {
                        Row {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.moon),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.surface,
                                modifier = Modifier
                                    .padding(end = 16.dp)
                                    .align(Alignment.CenterVertically)
                                    .size(24.dp)
                            )
                            SettingItemWithSwitch(
                                text = "Dark Mode",
                                isChecked = isDarkModeEnabled,
                                onCheckedChange = { isDarkModeEnabled = it }
                            )
                        }

                        SettingItemWithNavigation(
                            icon = ImageVector.vectorResource(id = R.drawable.globe),
                            text = "Language",
                            navController = navController,
                            destination = "language"
                        )
                        SettingItemWithNavigation(
                            icon = ImageVector.vectorResource(id = R.drawable.ic_noti),
                            text = "Notifications",
                            navController = navController,
                            destination = "notification_setting"
                        )
                        SettingItemWithNavigation(
                            icon = ImageVector.vectorResource(id = R.drawable.exclamation_circle),
                            text = "Report a problem",
                            navController = navController,
                            destination = "report_a_problem"
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // About Section
                    SettingsSection(title = "About") {
                        SettingItemWithNavigation(
                            icon = ImageVector.vectorResource(id = R.drawable.share),
                            text = "Share Account",
                            navController = navController,
                            destination = "share_account"
                        )
                        SettingItemWithNavigation(
                            icon = ImageVector.vectorResource(id = R.drawable.whatsapp),
                            text = "Terms of Service",
                            navController = navController,
                            destination = "terms_of_service"
                        )
                        SettingItemWithNavigation(
                            icon = ImageVector.vectorResource(id = R.drawable.lock),
                            text = "Privacy",
                            navController = navController,
                            destination = "privacy"
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    SettingsSection(title = "Danger Zone") {
                        SettingItemWithNavigation(
                            icon = ImageVector.vectorResource(id = R.drawable.trash3),
                            text = "Delete Account",
                            navController = navController,
                            destination = "confirm_password"
                        )
                        SettingItemWithNavigation(
                            icon = ImageVector.vectorResource(id = R.drawable.box_arrow_in_left),
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
            .padding(vertical = 8.dp)
            .clickable { navController.navigate(destination) } // Điều hướng đến trang khác
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .padding(end = 16.dp)
                .align(Alignment.CenterVertically)
                .size(24.dp)
        )
        Text(
            text = text,
            fontSize = 20.sp,
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.Bold,
            color = if (text == "Delete Account") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,
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
                color = if (option == "Remove Avatar") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,

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
