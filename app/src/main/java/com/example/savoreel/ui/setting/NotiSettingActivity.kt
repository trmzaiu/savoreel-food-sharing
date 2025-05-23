package com.example.savoreel.ui.setting

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.savoreel.model.ThemeViewModel
import com.example.savoreel.ui.component.BackArrow
import com.example.savoreel.ui.theme.SavoreelTheme
import com.example.savoreel.ui.theme.nunitoFontFamily

class NotiSettingActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeViewModel: ThemeViewModel = viewModel()
            val isDarkMode by themeViewModel.isDarkModeEnabled.collectAsState()
            SavoreelTheme(darkTheme = isDarkMode) {
                NotiSettingScreen()
            }
        }
    }
}

@Composable
fun NotiSettingScreen() {
    var isDisturb by rememberSaveable { mutableStateOf(false) }
    var isNewActivities by rememberSaveable { mutableStateOf(false) }
    var isSuggested by rememberSaveable { mutableStateOf(false) }
    var isReaction by rememberSaveable { mutableStateOf(false) }
    var isMemories by rememberSaveable { mutableStateOf(false) }
    var isShowPreviews by rememberSaveable { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)){
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
                    text = "Notification",
                    fontFamily = nunitoFontFamily,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 32.sp,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 40.dp, bottom = 10.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(modifier = Modifier.padding(horizontal = 35.dp)
            ) {
                item {
                    SettingsSection(title = "") {
                        SettingItemWithNavigation(
                            text = "Do Not Disturb",
                            changeMode = true,
                            isChecked = isDisturb,
                            onCheckedChange = { isDisturb = it }
                        )
                    }

                    SettingsSection(title = "Communities") {
                        SettingItemWithNavigation(
                            text = "New activities",
                            changeMode = true,
                            isChecked = isNewActivities,
                            onCheckedChange = { isNewActivities = it }
                        )
                        SettingItemWithNavigation(
                            text = "Suggested for you",
                            changeMode = true,
                            isChecked = isSuggested,
                            onCheckedChange = { isSuggested = it }
                        )
                        SettingItemWithNavigation(
                            text = "Reaction",
                            changeMode = true,
                            isChecked = isReaction,
                            onCheckedChange = { isReaction = it }
                        )
                    }

                    SettingsSection(title = "") {
                        SettingItemWithNavigation(
                            text = "Memories",
                            changeMode = true,
                            isChecked = isMemories,
                            onCheckedChange = { isMemories = it }
                        )
                    }

                    SettingsSection(title = "") {
                        SettingItemWithNavigation(
                            text = "Show previews",
                            changeMode = true,
                            isChecked = isShowPreviews,
                            onCheckedChange = { isShowPreviews = it }
                        )
                    }
                }
            }
        }
    }
}