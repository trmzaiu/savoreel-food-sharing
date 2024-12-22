package com.example.savoreel.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
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
import com.example.savoreel.ui.onboarding.VerifyCodeTheme
import com.example.savoreel.ui.profile.FollowScreen
import com.example.savoreel.ui.profile.ProfilePicturesData
import com.example.savoreel.ui.profile.ProfileScreen
import com.example.savoreel.ui.setting.NotificationSetting
import com.example.savoreel.ui.setting.SettingsScreen
import com.example.savoreel.ui.setting.TermsOfServiceScreen
import com.example.savoreel.ui.theme.SavoreelTheme
import com.example.savoreel.ui.theme.ThemeViewModel

@Composable
fun AppNavigation(navController: NavHostController, themeViewModel: ThemeViewModel, userViewModel: UserViewModel) {
    val isDarkModeEnabled by themeViewModel.isDarkModeEnabled

    SavoreelTheme(darkTheme = isDarkModeEnabled) {
        NavHost(
            navController = navController,
//        startDestination = "sign_in_screen",
            startDestination = "onboarding",
        ) {
            composable("onboarding") {
                OnboardingTheme(navController = navController)
            }

            composable("sign_in_screen") {backStackEntry ->
                SignInScreenTheme(
                    navController = navController,
                    userViewModel = userViewModel,
                )
            }

            composable("sign_up_screen") {
                SignUpScreenTheme(navController = navController)
            }

            composable("success_screen") {
                SuccessTheme(navController = navController)
            }

            composable("hashtag_screen") {
                HashTagTheme(navController = navController)
            }

            composable("settings_screen/{userId}") {backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull() ?: -1

                SettingsScreen(navController, themeViewModel, userViewModel, userId)
            }

            composable("name_screen/{userId}?isChangeName={isChangeName}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId")?.toInt() ?: 1
                val isChangeName = backStackEntry.arguments?.getString("isChangeName")?.toBoolean() ?: false

                NameTheme(
                    navController = navController,
                    isChangeName = isChangeName,
                    userViewModel = userViewModel,
                    userId = userId,
                    onNameSubmitted = {
                        if (isChangeName) {
                            navController.popBackStack()
                        } else {
                            navController.navigate("success_screen")
                        }
                    }
                )
            }

            composable("password_screen/{userId}?actionType={actionType}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId")?.toInt() ?: -1
                val actionType = backStackEntry.arguments?.getString("actionType") ?: ""

                PasswordTheme(
                    navController = navController,
                    userViewModel = userViewModel,
                    userId = userId,
                    onPasswordVerified = {
                        when (actionType) {
                            "change_email" -> {
                                navController.navigate("email_screen/${userId}?isChangeEmail=true") {
                                    popUpTo("password_screen") { inclusive = true }
                                }
                            }
                            "change_password" -> {
                                navController.navigate("change_password_screen/${userId}") {
                                    popUpTo("password_screen") { inclusive = true }
                                }
                            }
                        }
                    }
                )
            }

            composable("email_screen/{userId}?isChangeEmail={isChangeEmail}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId")?.toInt() ?: -1
                val isChangeEmail = backStackEntry.arguments?.getString("isChangeEmail")?.toBoolean() ?: false

                EmailTheme(navController, isChangeEmail, userViewModel, userId)
            }

            composable("verify_code_screen") {
                VerifyCodeTheme(navController)
            }

            composable("change_password_screen/{userId}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId")?.toInt() ?: -1

                ChangePasswordTheme(navController, userViewModel, userId)
            }

            composable("notification"){
                Notifications(navController = navController)
            }

            composable("searching"){
                Searching(navController = navController)
            }
        composable("notification"){
            Notifications(navController = navController)
        }
        composable("takephoto_screen") {
            PostView(navController = navController)
        }
        composable("searching"){
            Searching(navController = navController)
        }

            composable("searching_result/{query}", arguments = listOf(navArgument("query") { type = NavType.StringType })) { backStackEntry ->
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

            composable("profile_screen") {
                val profile = mapOf(
                    "24/2/2024" to listOf(
                        ProfilePicturesData("Title", "Description", "9:41 AM"),
                        ProfilePicturesData("Title", "Description", "9:41 AM"),
                        ProfilePicturesData("Title", "Description", "9:41 AM"),
                        ProfilePicturesData("Title", "Description", "9:41 AM"),
                        ProfilePicturesData("Title", "Description", "9:41 AM"),
                        ProfilePicturesData("Title", "Description", "9:41 AM")
                    ))
                ProfileScreen(profile = profile, navController = navController)
            }
        }
    }
}

@Composable
fun ScreenTransition(navController: NavHostController, destination: String) {
    val transition = remember { MutableTransitionState(false) }

    // Cập nhật trạng thái khi màn hình thay đổi
    LaunchedEffect(destination) {
        transition.targetState = true
    }

    // Thêm hiệu ứng hoạt ảnh khi chuyển giữa các màn hình
    AnimatedVisibility(
        visibleState = transition,
        enter = fadeIn() + slideInHorizontally(initialOffsetX = { it }),
        exit = fadeOut() + slideOutHorizontally(targetOffsetX = { -it })
    ) {
        // Điều hướng đến màn hình đích
        navController.navigate(destination)
    }
}

