package com.example.savoreel.ui.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
    userId: String
) {
    var email by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }

    LaunchedEffect(userId, isChangeEmail) {
        fun getUser(userId: String) {
            userViewModel.getUser(userId, onSuccess = { user ->
                user?.let {
                    email = it.email ?: ""
                }
            }, onFailure = { error ->
                errorMessage = error
                isError = true
            })
        }

        if (isChangeEmail) {
            getUser(userId)
        }
    }

    // Email validation logic
    fun isEmailValid(email: String): Boolean {
        return email.contains("@") && email.contains(".")
    }

    fun changeEmail() {
        userViewModel.updateUserEmail(userId, email, onSuccess = {
            println("Email successfully updated to: $email")
            navController.popBackStack()
        },
            onFailure = { error ->
                errorMessage = error
                showErrorDialog = true
            }
        )
    }

    fun resetPassword() {
        userViewModel.sendPasswordResetEmail(email, onSuccess = {
            println("Password reset email sent to: $email")
            navController.navigate("sign_in_screen")
        }, onFailure = { error ->
            errorMessage = error
            showErrorDialog = true
        })
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
                changeEmail()
            } else {
                resetPassword()
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

