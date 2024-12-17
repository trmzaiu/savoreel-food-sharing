package com.example.savoreel.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.savoreel.ui.onboarding.SignInScreenTheme
import com.example.savoreel.ui.onboarding.SignUpScreenTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.savoreel.ui.onboarding.ChangePasswordTheme
import com.example.savoreel.ui.onboarding.EmailTheme
import com.example.savoreel.ui.onboarding.NameTheme
import com.example.savoreel.ui.onboarding.OnboardingTheme
import com.example.savoreel.ui.onboarding.VerifyCodeTheme

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
//        startDestination = "sign_in_screen",
        startDestination = "onboarding"
    ) {
        composable("sign_in_screen") {
            SignInScreenTheme(navController = navController)
        }

        composable("sign_up_screen") {
            SignUpScreenTheme(navController = navController)
        }

        composable("email_screen?isChangeEmail={isChangeEmail}") { backStackEntry ->
            val isChangeEmail = backStackEntry.arguments?.getString("isChangeEmail")?.toBoolean() ?: false
            EmailTheme(navController = navController, isChangeEmail = isChangeEmail)
        }

        composable("verify_code_screen") {
            VerifyCodeTheme(navController = navController)
        }

        composable("change_password_screen") {
            ChangePasswordTheme(navController = navController)
        }

        composable("name_screen/{currentName}") { backStackEntry ->
            val currentName = backStackEntry.arguments?.getString("currentName")
            NameTheme(
                navController = navController,
                currentName = currentName
            ) { newName ->
                println("Submitted name: $newName")
            }
        }

        composable("onboarding") {
            OnboardingTheme()
        }
    }
}


