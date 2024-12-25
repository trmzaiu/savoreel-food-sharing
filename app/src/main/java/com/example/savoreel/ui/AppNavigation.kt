package com.example.savoreel.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.savoreel.model.ThemeViewModel
import com.example.savoreel.model.UserViewModel
import com.example.savoreel.ui.home.Notifications
import com.example.savoreel.ui.home.PostView
import com.example.savoreel.ui.home.Searching
import com.example.savoreel.ui.home.SearchingResult
import com.example.savoreel.ui.onboarding.ChangePasswordTheme
import com.example.savoreel.ui.onboarding.EmailTheme
import com.example.savoreel.ui.onboarding.HashTagTheme
import com.example.savoreel.ui.onboarding.NameTheme
import com.example.savoreel.ui.onboarding.OnboardingTheme
import com.example.savoreel.ui.onboarding.PasswordTheme
import com.example.savoreel.ui.onboarding.SignInScreenTheme
import com.example.savoreel.ui.onboarding.SignUpScreenTheme
import com.example.savoreel.ui.onboarding.SuccessTheme
import com.example.savoreel.ui.profile.FollowScreen
import com.example.savoreel.ui.profile.ProfileScreen
import com.example.savoreel.ui.setting.NotificationSetting
import com.example.savoreel.ui.setting.SettingTheme
import com.example.savoreel.ui.setting.TermsOfServiceScreen
import com.example.savoreel.ui.theme.SavoreelTheme
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavigation(navController: NavHostController, themeViewModel: ThemeViewModel, userViewModel: UserViewModel) {
    val isDarkModeEnabled by themeViewModel.isDarkModeEnabled.observeAsState(initial = false)
    val currentUser = FirebaseAuth.getInstance().currentUser

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            themeViewModel.loadUserSettings()
        }
    }

    val startDestination = when {
        currentUser != null -> "take_photo_screen"
        else -> "onboarding"
    }

    SavoreelTheme(darkTheme = isDarkModeEnabled) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
        ) {
            composable("onboarding") {
                OnboardingTheme(navController)
            }

            composable("sign_in_screen") {
                SignInScreenTheme(navController, userViewModel)
            }

            composable("sign_up_screen") {
                SignUpScreenTheme(navController, userViewModel)
            }

            composable("success_screen") {
                SuccessTheme(navController)
            }

            composable("hashtag_screen") {
                HashTagTheme(navController)
            }

            composable("name_screen?isChangeName={isChangeName}") { backStackEntry ->
                val isChangeName = backStackEntry.arguments?.getString("isChangeName")?.toBoolean() ?: false

                NameTheme(
                    navController, userViewModel, isChangeName,
                    onNameSubmitted = {
                        if (isChangeName) {
                            navController.popBackStack()
                        } else {
                            navController.navigate("success_screen")
                        }
                    }
                )
            }

            composable("password_screen?isChangePassword={isChangePassword}") { backStackEntry ->
                val isChangePassword = backStackEntry.arguments?.getString("isChangePassword")?.toBoolean() ?: false

                PasswordTheme(navController, userViewModel, isChangePassword)
            }

            composable("email_screen?isChangeEmail={isChangeEmail}") { backStackEntry ->
                val isChangeEmail = backStackEntry.arguments?.getString("isChangeEmail")?.toBoolean() ?: false

                EmailTheme(navController, isChangeEmail, userViewModel)
            }

            composable("change_password_screen") {
                ChangePasswordTheme(navController, userViewModel)
            }

            composable("take_photo_screen") {
                PostView(navController, userViewModel)
            }

            composable("profile_screen") {
                ProfileScreen(navController, userViewModel)
            }

            composable("settings_screen") {
                SettingTheme(navController, themeViewModel, userViewModel)
            }

            composable("searching") {
                Searching(navController)
            }

            composable("notification") {
                Notifications(navController)
            }

            composable(
                "searching_result/{query}",
                arguments = listOf(navArgument("query") { type = NavType.StringType })
            ) { backStackEntry ->
                val query = backStackEntry.arguments?.getString("query") ?: ""
                SearchingResult(navController = navController, searchQuery = query)
            }

            composable("notification_setting") {
                NotificationSetting(navController = navController)
            }

            composable("follow") {
                FollowScreen(navController = navController)
            }

            composable("terms_of_service") {
                TermsOfServiceScreen(navController = navController)
            }


        }
    }
}

