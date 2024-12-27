//package com.example.savoreel.ui
//
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.livedata.observeAsState
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import com.example.savoreel.model.NotificationViewModel
//import com.example.savoreel.model.ThemeViewModel
//import com.example.savoreel.model.UserViewModel
//import com.example.savoreel.ui.home.GridPost
//import com.example.savoreel.ui.home.PostView
//import com.example.savoreel.ui.onboarding.ChangePasswordTheme
//import com.example.savoreel.ui.onboarding.EmailTheme
//import com.example.savoreel.ui.onboarding.HashTagTheme
//import com.example.savoreel.ui.onboarding.NameTheme
//import com.example.savoreel.ui.onboarding.OnboardingTheme
//import com.example.savoreel.ui.onboarding.PasswordTheme
//import com.example.savoreel.ui.onboarding.SignInScreenTheme
//import com.example.savoreel.ui.onboarding.SignUpScreenTheme
//import com.example.savoreel.ui.onboarding.SuccessTheme
//import com.example.savoreel.ui.profile.FollowScreen
//import com.example.savoreel.ui.setting.NotificationSetting
//import com.example.savoreel.ui.setting.SettingTheme
//import com.example.savoreel.ui.setting.TermsOfServiceScreen
//import com.example.savoreel.ui.theme.SavoreelTheme
//import com.google.firebase.auth.FirebaseAuth
//
//@Composable
//fun AppNavigation(navController: NavHostController, themeViewModel: ThemeViewModel, userViewModel: UserViewModel, notificationViewModel: NotificationViewModel = viewModel()) {
//    val isDarkModeEnabled by themeViewModel.isDarkModeEnabled.observeAsState(initial = false)
//    val currentUser = FirebaseAuth.getInstance().currentUser
//    var currentUserId by remember { mutableStateOf("") }
//    var startDestination by remember { mutableStateOf("") }
//
//    LaunchedEffect(currentUser) {
//        if (currentUser != null) {
//            themeViewModel.loadUserSettings()
//        }
//    }
//    if (currentUser != null){
//        startDestination = "take_photo_screen"
//        currentUserId = currentUser.uid
//    } else {
//        startDestination = "onboarding"
//    }
//
//    SavoreelTheme(darkTheme = isDarkModeEnabled) {
//        NavHost(
//            navController = navController,
//            startDestination = startDestination,
//        ) {
//            composable("onboarding") {
//                OnboardingTheme(navController)
//            }
//
//            composable("sign_in_screen") {
//                SignInScreenTheme(navController, userViewModel)
//            }
//
//            composable("sign_up_screen") {
//                SignUpScreenTheme(navController, userViewModel)
//            }
//
//            composable("success_screen") {
//                SuccessTheme(navController)
//            }
//
//            composable("hashtag_screen") {
//                HashTagTheme(navController)
//            }
//
//            composable("name_screen?isChangeName={isChangeName}") { backStackEntry ->
//                val isChangeName = backStackEntry.arguments?.getString("isChangeName")?.toBoolean() ?: false
//
//                NameTheme(
//                    navController, userViewModel, isChangeName,
//                    onNameSubmitted = {
//                        if (isChangeName) {
//                            navController.popBackStack()
//                        } else {
//                            navController.navigate("success_screen")
//                        }
//                    }
//                )
//            }
//
//            composable("password_screen?isChangePassword={isChangePassword}") { backStackEntry ->
//                val isChangePassword = backStackEntry.arguments?.getString("isChangePassword")?.toBoolean() ?: false
//
//                PasswordTheme(navController, userViewModel, isChangePassword)
//            }
//
//            composable("email_screen?isChangeEmail={isChangeEmail}") { backStackEntry ->
//                val isChangeEmail = backStackEntry.arguments?.getString("isChangeEmail")?.toBoolean() ?: false
//
//                EmailTheme(navController, isChangeEmail, userViewModel)
//            }
//
//            composable("change_password_screen") {
//                ChangePasswordTheme(navController, userViewModel)
//            }
//
//            composable("take_photo_screen") {
//                PostView(navController, userViewModel)
//            }
////
////            composable("profile_screen") {
////                ProfileScreen(navController, userViewModel)
////            }
//
//            composable("settings_screen") {
//                SettingTheme(navController, themeViewModel, userViewModel)
//            }
//
////            composable("notification") {
////                NotificationTheme(navController, notificationViewModel, userViewModel)
////            }
////
////            composable("search_result/{query}") { backStackEntry ->
////                val query = backStackEntry.arguments?.getString("query").orEmpty()
////                SearchingResult(navController, query, userViewModel)
////            }
//
//            composable("notification_setting") {
//                NotificationSetting(navController = navController)
//            }
//
//            composable("follow/{query}/{query1}") {backStackEntry ->
//                val query = backStackEntry.arguments?.getString("query").orEmpty()
//                val query1 = backStackEntry.arguments?.getString("query1").orEmpty()
//                FollowScreen(navController = navController, userViewModel, query, query1)
//            }
//
//            composable("terms_of_service") {
//                TermsOfServiceScreen(navController = navController)
//            }
//
//            composable("grid_post/{query}"){ backStackEntry ->
//                val query = backStackEntry.arguments?.getString("query").orEmpty()
//                GridPost(query, navController, userViewModel)
//            }
//        }
//    }
//}
