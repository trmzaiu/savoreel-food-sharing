package com.example.savoreel.ui.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.savoreel.ui.component.BackArrow
import com.example.savoreel.ui.theme.SavoreelTheme
import com.example.savoreel.ui.theme.nunitoFontFamily
import com.example.savoreel.ui.component.SettingItemWithSwitch

@Composable
fun NotificationSetting(navController: NavController) {
    var isDisturb by rememberSaveable { mutableStateOf(false) }
    var isNewActivities by rememberSaveable { mutableStateOf(false) }
    var isSuggested by rememberSaveable { mutableStateOf(false) }
    var isReaction by rememberSaveable { mutableStateOf(false) }
    var isMemories by rememberSaveable { mutableStateOf(false) }
    var isShowPreviews by rememberSaveable { mutableStateOf(false) }

    SavoreelTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 20.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
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
                    // Do Not Disturb Section
                    SettingNotiSection(title = "") {
                        SettingItemWithSwitch(
                            text = "Do Not Disturb",
                            isChecked = isDisturb,
                            onCheckedChange = { isDisturb = it }
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))

                    // Communities Section
                    SettingNotiSection(title = "Communities") {
                        SettingItemWithSwitch(
                            text = "New activities",
                            isChecked = isNewActivities,
                            onCheckedChange = { isNewActivities = it }
                        )
                        SettingItemWithSwitch(
                            text = "Suggested for you",
                            isChecked = isSuggested,
                            onCheckedChange = { isSuggested = it }
                        )
                        SettingItemWithSwitch(
                            text = "Reaction",
                            isChecked = isReaction,
                            onCheckedChange = { isReaction = it }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Memories Section
                    SettingNotiSection(title = "") {
                        SettingItemWithSwitch(
                            text = "Memories",
                            isChecked = isMemories,
                            onCheckedChange = { isMemories = it }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Show Previews Section
                    SettingNotiSection(title = "") {
                        SettingItemWithSwitch(
                            text = "Show previews",
                            isChecked = isShowPreviews,
                            onCheckedChange = { isShowPreviews = it }
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
        Text(
            text = title,
            fontSize = 20.sp,
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier
                .padding(bottom = 8.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationSettingPreview() {
    NotificationSetting(navController = rememberNavController())
}
