package com.example.savoreel.ui.setting

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.example.savoreel.ui.component.SettingItemWithSwitch
import com.example.savoreel.ui.theme.SavoreelTheme
import com.example.savoreel.ui.theme.ThemeViewModel
import com.example.savoreel.ui.theme.nunitoFontFamily

@Composable
fun NotificationSetting(navController: NavController, themeViewModel: ThemeViewModel) {
    val isDarkModeEnabled = themeViewModel.isDarkModeEnabled
    Log.i("NotificationSetting", "isDarkModeEnabled: $isDarkModeEnabled") // Log giá trị dark mode hiện tại

    var isDisturb by rememberSaveable { mutableStateOf(false) }
    var isNewActivities by rememberSaveable { mutableStateOf(false) }
    var isSuggested by rememberSaveable { mutableStateOf(false) }
    var isReaction by rememberSaveable { mutableStateOf(false) }
    var isMemories by rememberSaveable { mutableStateOf(false) }
    var isShowPreviews by rememberSaveable { mutableStateOf(false) }

    SavoreelTheme(darkTheme = isDarkModeEnabled) {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)){
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = MaterialTheme.colorScheme.background)
//                        .shadow(elevation = 4.dp, spotColor = Color(0x80000000), ambientColor = Color(0x40000000))
                ) {
                    BackArrow(
                        navController = navController,
                        modifier = Modifier.align(Alignment.TopStart)
                    )
                    Text(
                        text = "Notification",
                        fontFamily = nunitoFontFamily,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 32.sp,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(top = 40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                LazyColumn(modifier = Modifier.padding(horizontal = 20.dp)
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
}

@Composable
fun SettingNotiSection(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier
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
    NotificationSetting(navController = rememberNavController(), themeViewModel = ThemeViewModel())
}
