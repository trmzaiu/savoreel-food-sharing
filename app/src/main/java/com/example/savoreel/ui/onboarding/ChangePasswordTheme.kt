package com.example.savoreel.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.savoreel.model.UserViewModel
import com.example.savoreel.ui.component.BackArrow
import com.example.savoreel.ui.component.CustomButton
import com.example.savoreel.ui.component.CustomInputField
import com.example.savoreel.ui.component.CustomTitle
import com.example.savoreel.ui.component.ErrorDialog

@Composable
fun ChangePasswordTheme(navController: NavController, userViewModel: UserViewModel, userId: Int) {
    val user = userViewModel.findUserById(userId)
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    val isFormValid = password.isNotEmpty() && confirmPassword.isNotEmpty() && confirmPassword.length >= password.length

    fun isPasswordValid(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        BackArrow(
            navController = navController,
            modifier = Modifier.align(Alignment.TopStart)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(20.dp)
        ) {
            CustomTitle(
                text = "Enter new password"
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Password Input
            CustomInputField(
                value = password,
                onValueChange = { password = it },
                placeholder = "Password",
                isPasswordField = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            CustomInputField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = "Confirm password",
                isPasswordField = true
            )

            Spacer(modifier = Modifier.height(100.dp))

            // Button
            CustomButton(
                text = "Confirm",
                enabled = isFormValid,
                onClick = {
                    if (!isPasswordValid(password, confirmPassword)) {
                        errorMessage = "The password and confirm password do not match. Please check and try again."
                        showErrorDialog = true
                    } else {
                        errorMessage = "Change password successfully!"
                        showConfirmDialog = true
                        println("Password: $password, $confirmPassword")
                    }
                }
            )

            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    if (showErrorDialog) {
        ErrorDialog(
            title = "Password mismatch",
            message = errorMessage,
            onDismiss = { showErrorDialog = false }
        )
    }

    if (showConfirmDialog) {
        ErrorDialog(
            title = "Success",
            message = errorMessage,
            onDismiss = {
                showConfirmDialog = false
                navController.navigate("sign_in_screen") {
                    popUpTo("sign_in_screen") { inclusive = true }
                }
            }
        )
    }
}

//@Preview(showBackground = true)
//@Composable
//fun ResetPasswordDarkPreview() {
//    SavoreelTheme(darkTheme = true) {
//        ChangePasswordTheme(navController = rememberNavController())
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun ResetPasswordLightPreview() {
//    SavoreelTheme(darkTheme = false) {
//        ChangePasswordTheme(navController = rememberNavController())
//    }
//}
