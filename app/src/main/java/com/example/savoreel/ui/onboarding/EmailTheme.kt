package com.example.savoreel.ui.onboarding

import android.util.Patterns
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.example.savoreel.model.UserViewModel
import com.example.savoreel.ui.component.CommonForm
import com.example.savoreel.ui.component.ErrorDialog

@Composable
fun EmailTheme(
    navController: NavController,
    isChangeEmail: Boolean,
    userViewModel: UserViewModel,
    userId: Int
) {
    val user = userViewModel.findUserById(userId)
    var email by remember { mutableStateOf(user?.email ?: "") }
    var errorMessage by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }

    fun validateEmail(email: String): Boolean {
        return user?.email == email
    }

    fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    CommonForm(
        navController = navController,
        title = if (isChangeEmail) "Change your email" else "Enter your email",
        placeholder = "Email",
        buttonText = if (isChangeEmail) "Save" else "Continue",
        value = email,
        onValueChange = { email = it },
        isPasswordField = false,
        isButtonEnabled = isEmailValid(email),
        onClickButton = {
            if (isChangeEmail) {
                println("Email changed to: $email")
                navController.popBackStack()
            } else {
                if (!validateEmail(email)) {
                    errorMessage = "Make sure you entered your email correctly and try again."
                    showErrorDialog = true
                } else {
                    navController.navigate("verify_code_screen") {
                        popUpTo("email_screen") { inclusive = true }
                    }
                }
            }
        },
        additionalContent = {
            if (showErrorDialog) {
                ErrorDialog(
                    title = "Couldn't sign in",
                    message = errorMessage,
                    onDismiss = { showErrorDialog = false }
                )
            }
        }
    )
}

//@Preview(showBackground = true)
//@Composable
//fun EmailDarkPreview() {
//    SavoreelTheme(darkTheme = true) {
//        EmailTheme(navController = rememberNavController(), isChangeEmail = false)
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun ChangeEmailDarkPreview() {
//    SavoreelTheme(darkTheme = true) {
//        EmailTheme(navController = rememberNavController(), isChangeEmail = true, currentEmail = "example@gmail.com")
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun EmailLightPreview() {
//    SavoreelTheme(darkTheme = false) {
//        EmailTheme(navController = rememberNavController(), isChangeEmail = false)
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun ChangeEmailLightPreview() {
//    SavoreelTheme(darkTheme = false) {
//        EmailTheme(navController = rememberNavController(), isChangeEmail = true, currentEmail = "example@gmail.com")
//    }
//}

