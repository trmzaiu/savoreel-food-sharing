package com.example.savoreel.ui.setting

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.savoreel.R
import com.example.savoreel.model.UserViewModel
import com.example.savoreel.ui.component.BackArrow
import com.example.savoreel.ui.component.CustomSwitch
import com.example.savoreel.ui.component.ForwardArrow
import com.example.savoreel.ui.component.IconTheme
import com.example.savoreel.ui.theme.SavoreelTheme
import com.example.savoreel.ui.theme.ThemeViewModel
import com.example.savoreel.ui.theme.nunitoFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, themeViewModel: ThemeViewModel, userViewModel: UserViewModel, userId: Int) {
    var showModal by remember { mutableStateOf(false) }
    val isDarkModeEnabled by remember { themeViewModel.isDarkModeEnabled }

    val user = userViewModel.findUserById(userId)

    SavoreelTheme(darkTheme = isDarkModeEnabled) {
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
                         navController = navController,
                         modifier = Modifier.align(Alignment.TopStart)
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
                             .padding(top = 40.dp)
                     )
                 }

                 LazyColumn(
                     modifier = Modifier.padding(horizontal = 35.dp)
                 ) {
                     item {
                         Spacer(modifier = Modifier.height(30.dp))

                         Box(
                             contentAlignment = Alignment.Center,
                             modifier = Modifier
                                 .fillMaxWidth()
                                 .align(Alignment.CenterHorizontally)
                         ) {
                             Image(
                                 painter = painterResource(id = R.drawable.default_avatar),
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
                             ){
                                 ModalBottomSheet(
                                     onDismissRequest = { showModal = false },
                                     sheetState = rememberModalBottomSheetState(
                                         skipPartiallyExpanded = true
                                     ),
                                     containerColor = MaterialTheme.colorScheme.secondary
                                 )
                                 {
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

                         if (user != null) {
                             Text(
                                 text = user.name,
                                 fontFamily = nunitoFontFamily,
                                 textAlign = TextAlign.Center,
                                 fontWeight = FontWeight.Bold,
                                 fontSize = 24.sp,
                                 color = MaterialTheme.colorScheme.onBackground,
                                 modifier = Modifier.fillMaxWidth()
                             )
                         } else {
                             Text(
                                 text = "User not found",
                                 fontFamily = nunitoFontFamily,
                                 textAlign = TextAlign.Center,
                                 fontWeight = FontWeight.Bold,
                                 fontSize = 24.sp,
                                 color = MaterialTheme.colorScheme.error,
                                 modifier = Modifier.fillMaxWidth()
                             )
                         }

                         // General Section
                         Spacer(modifier = Modifier.height(20.dp))

                         SettingsSection(title = "General") {
                             SettingItemWithNavigation(
                                 icon = ImageVector.vectorResource(id = R.drawable.ic_name),
                                 text = "Edit Name",
                                 navController = navController,
                                 destination = "name_screen/${userId}?isCreateMode=false",
                             )
                             SettingItemWithNavigation(
                                 icon = ImageVector.vectorResource(id = R.drawable.ic_mail),
                                 text = "Change Email",
                                 navController = navController,
                                 destination = "password_screen/${userId}?actionType=change_email",
                             )
                             SettingItemWithNavigation(
                                 icon = ImageVector.vectorResource(id = R.drawable.ic_key),
                                 text = "Change Password",
                                 navController = navController,
                                 destination = "password_screen/${userId}?actionType=change_password",
                             )
                         }

                         // Support Section
                         SettingsSection(title = "Support") {
                             SettingItemWithNavigation(
                                 icon = ImageVector.vectorResource(id = R.drawable.ic_darkmode),
                                 text = "Dark Mode",
                                 changeMode = true,
                                 isChecked = isDarkModeEnabled,
                                 onCheckedChange = { themeViewModel.toggleDarkMode() }
                             )

                             SettingItemWithNavigation(
                                 icon = ImageVector.vectorResource(id = R.drawable.ic_language),
                                 text = "Language",
                                 navController = navController,
                                 destination = "language",
                             )
                             SettingItemWithNavigation(
                                 icon = ImageVector.vectorResource(id = R.drawable.ic_noti),
                                 text = "Notifications",
                                 navController = navController,
                                 destination = "notification_setting",
                             )
                             SettingItemWithNavigation(
                                 icon = ImageVector.vectorResource(id = R.drawable.ic_report),
                                 text = "Report a problem",
                                 navController = navController,
                                 destination = "report_a_problem",
                             )
                         }

                         // About Section
                         SettingsSection(title = "About") {
                             SettingItemWithNavigation(
                                 icon = ImageVector.vectorResource(id = R.drawable.ic_share),
                                 text = "Share Account",
                                 navController = navController,
                                 destination = "share_account",
                             )
                             SettingItemWithNavigation(
                                 icon = ImageVector.vectorResource(id = R.drawable.ic_term),
                                 text = "Terms of Service",
                                 navController = navController,
                                 destination = "terms_of_service",
                             )
                             SettingItemWithNavigation(
                                 icon = ImageVector.vectorResource(id = R.drawable.ic_privacy),
                                 text = "Privacy",
                                 navController = navController,
                                 destination = "privacy",
                             )
                         }

                         SettingsSection(title = "Danger Zone") {
                             SettingItemWithNavigation(
                                 icon = ImageVector.vectorResource(id = R.drawable.ic_delete),
                                 text = "Delete Account",
                                 navController = navController,
                                 destination = "confirm_password",
                             )
                             SettingItemWithNavigation(
                                 icon = ImageVector.vectorResource(id = R.drawable.ic_logout),
                                 text = "Sign Out",
                                 navController = navController,
                                 destination = "sign_out",
                             )
                         }
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
    ) {
        Text(
            text = title,
            fontFamily = nunitoFontFamily,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier
                .padding(bottom = 4.dp)
        )
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
    navController: NavController? = null,
    destination: String? = null,
    changeMode: Boolean = false,
    isChecked: Boolean = false,
    onCheckedChange: (Boolean) -> Unit = {}
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp)
            .clickable {
                destination?.let {
                    navController?.navigate(it)
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            IconTheme(
                imageVector = icon,
                alpha = 0.8f
            )
            Spacer(modifier = Modifier.width(16.dp))
        }

        Text(
            text = text,
            modifier = Modifier.weight(1f),
            fontSize = 20.sp,
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.Bold,
            color = if (text == "Delete Account") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
        )

        if(!changeMode) {
            if (navController != null && destination != null) {
                ForwardArrow(
                    navController = navController,
                    destination = destination,
                )
            }
        } else {
            CustomSwitch(
                checked = isChecked,
                onCheckedChange = onCheckedChange
            )
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
        SettingsScreen(navController = rememberNavController(), themeViewModel = ThemeViewModel(), userViewModel = UserViewModel(), userId = 1) // Pass the navController as normal
    }
}
//
//@Preview(showBackground = true)
//@Composable
//fun SettingsScreenPreview() {
//    SavoreelTheme(darkTheme = false, dynamicColor = false) {
//        SettingsScreen(navController = rememberNavController(), themeViewModel = ThemeViewModel()) // Pass the navController as normal
//    }
//}
