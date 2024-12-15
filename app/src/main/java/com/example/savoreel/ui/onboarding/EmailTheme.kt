package com.example.savoreel.ui.onboarding

import android.util.Patterns
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.savoreel.ui.component.CommonForm
import com.example.savoreel.ui.component.ErrorDialog
import com.example.savoreel.ui.theme.SavoreelTheme

@Composable
fun EmailTheme(navController: NavController, isChangeEmail: Boolean, currentEmail: String = "") {
    var email by remember { mutableStateOf(if (isChangeEmail) currentEmail else "") }
    var errorMessage by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }

    val validEmail = "example@gmail.com"

    fun validateEmail(email: String): Boolean {
        return email == validEmail
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
                navController.navigate("profile_screen")
            } else {
                if (!validateEmail(email)) {
                    errorMessage = "Make sure you entered your email correctly and try again."
                    showErrorDialog = true
                } else {
                    navController.navigate("verify_code_screen")
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

@Preview(showBackground = true)
@Composable
fun EmailDarkPreview() {
    SavoreelTheme(darkTheme = true) {
        EmailTheme(navController = rememberNavController(), isChangeEmail = false)
    }
}

@Preview(showBackground = true)
@Composable
fun ChangeEmailDarkPreview() {
    SavoreelTheme(darkTheme = true) {
        EmailTheme(navController = rememberNavController(), isChangeEmail = true, currentEmail = "example@gmail.com")
    }
}

@Preview(showBackground = true)
@Composable
fun EmailLightPreview() {
    SavoreelTheme(darkTheme = false) {
        EmailTheme(navController = rememberNavController(), isChangeEmail = false)
    }
}

@Preview(showBackground = true)
@Composable
fun ChangeEmailLightPreview() {
    SavoreelTheme(darkTheme = false) {
        EmailTheme(navController = rememberNavController(), isChangeEmail = true, currentEmail = "example@gmail.com")
    }
}

