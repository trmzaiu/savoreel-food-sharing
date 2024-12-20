package com.example.savoreel.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class ThemeViewModel : ViewModel() {
    var isDarkModeEnabled by mutableStateOf(false)

    fun toggleDarkMode() {
        isDarkModeEnabled = !isDarkModeEnabled
    }

    fun setDarkMode(enabled: Boolean) {
        isDarkModeEnabled = enabled
    }
}
