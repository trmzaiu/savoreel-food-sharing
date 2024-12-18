package com.example.savoreel.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import com.example.savoreel.ui.onboarding.SignInScreenTheme
import com.example.savoreel.ui.onboarding.SignUpScreenTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.savoreel.ui.home.Notifications
import com.example.savoreel.ui.home.Searching
import com.example.savoreel.ui.home.SearchingResult
import com.example.savoreel.ui.onboarding.ChangePasswordTheme
import com.example.savoreel.ui.onboarding.EmailTheme
import com.example.savoreel.ui.onboarding.NameTheme
import com.example.savoreel.ui.onboarding.Onboarding2Theme
import com.example.savoreel.ui.onboarding.Onboarding3Preview
import com.example.savoreel.ui.onboarding.Onboarding3Theme
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

        composable("notification"){
            Notifications(navController = navController)
        }

        composable("searching"){
            Searching(navController = navController)
        }

        composable("searching_result/{query}", arguments = listOf(navArgument("query") { type = NavType.StringType })) { backStackEntry ->
            val query = backStackEntry.arguments?.getString("query") ?: ""
            SearchingResult(navController = navController, searchQuery = query)
        }


        composable("onboarding") {
            Onboarding3Theme(navController = navController)
        }
    }
}


