package com.example.savoreel.ui.setting

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.navigation.compose.*
import com.example.savoreel.ui.theme.backgroundLightColor
import com.example.savoreel.ui.theme.disableButtonColor
import com.example.savoreel.ui.theme.nunitoFontFamily
import com.example.savoreel.ui.theme.primaryButtonColor
import com.example.savoreel.ui.theme.secondaryLightColor

@Composable
fun SettingsScreen(navController: NavController) {
    var isDarkModeEnabled by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundLightColor)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center // Center items horizontally
        ) {
            // Back Button
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black,
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.CenterVertically) // Align vertically
            )
            // Centered Name
            Text(
                text = "Setting",
                fontFamily = nunitoFontFamily,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                fontSize = 32.sp,
                modifier = Modifier.weight(10f) // Weight for centering logic
            )
        }
        Spacer(modifier = Modifier.height(30.dp))
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "User Avatar",
            tint = Color.Gray,
            modifier = Modifier
                .size(150.dp)
                .fillMaxWidth()  // Đảm bảo chiếm toàn bộ chiều rộng
                .align(Alignment.CenterHorizontally)  // Căn giữa theo chiều ngang
        )

        Text (
            text = "Name",
            fontFamily = nunitoFontFamily,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier
                .fillMaxWidth()
        )
        // General Section
        SettingsSection(title = "General") {
            SettingItemWithNavigation(
                icon = Icons.Filled.Edit,
                text = "Edit Name",
                navController = navController,
                destination = "name"
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

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    NavHostSetup()
}
