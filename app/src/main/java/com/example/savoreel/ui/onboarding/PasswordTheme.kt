package com.example.savoreel.ui.onboarding

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
fun PasswordTheme(navController: NavController, userViewModel: UserViewModel, userId: String, isChangePassword: Boolean) {
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }

    fun validatePassword() {
        userViewModel.validatePassword(userId, password, onSuccess = {
            if (isChangePassword) {
                navController.navigate("email_screen/${userId}?isChangeEmail=true") {
                    popUpTo("password_screen") { inclusive = true }
                }
            } else {
                navController.navigate("change_password_screen/${userId}?isChangeEmail=false") {
                    popUpTo("password_screen") { inclusive = true }
                }
            }
        }, onFailure = { error ->
            errorMessage = error
            showErrorDialog = true
        })
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
            validatePassword()
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
//fun PasswordDarkPreview() {
//    SavoreelTheme(darkTheme = true) {
//        PasswordTheme(navController = rememberNavController())
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun PasswordLightPreview() {
//    SavoreelTheme(darkTheme = false) {
//        PasswordTheme(navController = rememberNavController())
//    }
//}


