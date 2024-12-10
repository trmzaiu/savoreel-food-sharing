package com.example.savoreel.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.savoreel.ui.onboarding.SignInScreenTheme
import com.example.savoreel.ui.onboarding.SignUpScreenTheme


import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "signin_screen"
    ) {
        composable("signin_screen") {
            SignInScreenTheme(navController = navController)
        }

        composable("signup_screen") {
            SignUpScreenTheme(navController = navController)
        }
    }
}


