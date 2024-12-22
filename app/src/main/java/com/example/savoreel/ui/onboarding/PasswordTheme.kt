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
fun PasswordTheme(navController: NavController, userViewModel: UserViewModel, userId: Int, onPasswordVerified: () -> Unit) {
    val user = userViewModel.findUserById(userId)
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }

    fun validatePassword(password: String): Boolean {
        return user?.password == password
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
                onPasswordVerified()
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


