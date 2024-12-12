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
fun PasswordTheme(navController: NavController, actionType: String ) {
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }

    val validPassword = "12345678"

    fun validatePassword(password: String): Boolean {
        return password == validPassword
    }

    CommonForm(
        navController = navController,
        title = "Enter your password",
        placeholder = "Password",
        buttonText = "Continue",
        value = password,
        onValueChange = { password = it },
        isPasswordField = true,
        isButtonEnabled = password.isNotEmpty(),
        onClickButton = {
            if (!validatePassword(password)) {
                errorMessage = "Make sure you entered your password correctly and try again."
                showErrorDialog = true
            } else {
                if (actionType == "change_email") {
                    navController.navigate("email_screen?isChangeEmail=true")
                } else {
                    navController.navigate("change_password_screen")
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
fun PasswordPreview() {
    SavoreelTheme {
        PasswordTheme(navController = rememberNavController(), actionType = "change_password")
    }
}


