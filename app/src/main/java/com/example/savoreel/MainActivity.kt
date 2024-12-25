package com.example.savoreel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.savoreel.model.ThemeViewModel
import com.example.savoreel.model.UserViewModel
import com.example.savoreel.ui.AppNavigation
import com.example.savoreel.ui.theme.SavoreelTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeViewModel: ThemeViewModel = viewModel()
            val userViewModel: UserViewModel = viewModel()
            val isDarkMode by themeViewModel.isDarkModeEnabled.observeAsState(initial = false)

            LaunchedEffect(Unit) {
                if (FirebaseAuth.getInstance().currentUser == null) {
                    themeViewModel.resetDarkMode()
                } else {
                    themeViewModel.loadUserSettings()
                }
            }

            SavoreelTheme(darkTheme = isDarkMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    themeViewModel.loadUserSettings()
                    AppNavigation(rememberNavController(), themeViewModel, userViewModel)
                }
            }
        }
    }
}


