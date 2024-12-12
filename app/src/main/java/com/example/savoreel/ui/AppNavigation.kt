package com.example.savoreel.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.savoreel.ui.onboarding.SignInScreenTheme
import com.example.savoreel.ui.onboarding.SignUpScreenTheme


import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.savoreel.ui.onboarding.ForgotPasswordTheme
import com.example.savoreel.ui.onboarding.ResetPasswordTheme
import com.example.savoreel.ui.onboarding.VerifyCodeTheme

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "sign_in_screen"
    ) {
        composable("sign_in_screen") {
            SignInScreenTheme(navController = navController)
        }

        composable("sign_up_screen") {
            SignUpScreenTheme(navController = navController)
        }

        composable("forgot_password_screen") {
            ForgotPasswordTheme(navController = navController)
        }

        composable("verify_code_screen") {
            VerifyCodeTheme(navController = navController)
        }

        composable("reset_password_screen") {
            ResetPasswordTheme(navController = navController)
        }
    }
}


