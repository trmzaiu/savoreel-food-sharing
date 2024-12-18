package com.example.savoreel.ui.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.savoreel.ui.component.BackArrow
import com.example.savoreel.ui.theme.SavoreelTheme
import com.example.savoreel.ui.theme.backgroundLightColor
import com.example.savoreel.ui.theme.nunitoFontFamily
import com.example.savoreel.ui.theme.secondaryLightColor
import com.example.savoreel.ui.theme.primaryButtonColor
import com.example.savoreel.ui.theme.disableButtonColor

@Composable
fun NotificationSetting(navController: NavController) {
    var isDarkModeEnabled by rememberSaveable { mutableStateOf(false) }  // Add this state
    SavoreelTheme(darkTheme = isDarkModeEnabled) {
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
                text = "Notification",
                fontFamily = nunitoFontFamily,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.tertiary,
                fontSize = 32.sp,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 40.dp)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(20.dp))

                // Do Not Disturb Section
                Box(
                    modifier = Modifier
                        .background(color = MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(10.dp))
                        .padding(horizontal = 16.dp)
                ) {
                    SettingItemWithSwitch(
                        text = "Do Not Disturb",
                    )
                }


                Spacer(modifier = Modifier.height(40.dp))

                // Communities Group
                SettingNotiSection(title = "Communities") {
                    SettingItemWithSwitch(
                        text = "New activities"
                    )
                    SettingItemWithSwitch(
                        text = "Suggested for you"
                    )
                    SettingItemWithSwitch(
                        text = "Reaction"
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Do Not Disturb Section
                Box(
                    modifier = Modifier
                        .background(secondaryLightColor, shape = RoundedCornerShape(10.dp))
                        .padding(horizontal = 16.dp)
                ) {
                    SettingItemWithSwitch(
                        text = "Memories",
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Do Not Disturb Section
                Box(
                    modifier = Modifier
                        .background(secondaryLightColor, shape = RoundedCornerShape(10.dp))
                        .padding(horizontal = 16.dp)
                ) {
                    SettingItemWithSwitch(
                        text = "Show previews",
                    )
                }
            }
        }
    }
}
    }

@Composable
fun SettingNotiSection(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Title of the section
        Text(
            text = title,
            fontSize = 20.sp,
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier
                .padding(bottom = 8.dp)
        )

        // Background color for the section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(10.dp))
                .padding(16.dp)
        ) {
            content()
        }
    }
}

@Composable
fun SettingItemWithSwitch(text: String, isChecked: Boolean = false, onCheckedChange: (Boolean) -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .height(48.dp)
    ) {
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
                checkedThumbColor = MaterialTheme.colorScheme.secondary,  // Màu khi bật
                uncheckedThumbColor = MaterialTheme.colorScheme.secondary,  // Màu khi tắt
                checkedTrackColor = MaterialTheme.colorScheme.primary,  // Màu track khi bật
                uncheckedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)  // Màu track khi tắt
            ),
            modifier = Modifier
                .align(Alignment.CenterVertically)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationSettingPreview() {
    NotificationSetting(navController = rememberNavController())
}
