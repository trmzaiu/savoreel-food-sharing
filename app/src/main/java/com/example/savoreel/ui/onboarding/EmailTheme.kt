package com.example.savoreel.ui.onboarding

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.example.savoreel.model.UserViewModel
import com.example.savoreel.ui.component.CommonForm

@Composable
fun EmailTheme(
    navController: NavController,
    isChangeEmail: Boolean,
    userViewModel: UserViewModel,
) {
    var email by remember { mutableStateOf("") }

    LaunchedEffect(Unit, isChangeEmail) {
        if (isChangeEmail) {
            userViewModel.getUser(onSuccess = { user ->
                if (user != null) {
                    email = user.email ?: ""
                } else {
                    Log.e("EmailTheme", "User data not found")
                }
            }, onFailure = { error ->
                Log.e("EmailTheme", "Error retrieving user: $error")
            })
        }
    }

    // Email validation logic
    fun isEmailValid(email: String): Boolean {
        return email.contains("@") && email.contains(".")
    }

    fun changeEmail() {
        userViewModel.updateUserEmail(email, onSuccess = {
            navController.popBackStack()
        },
            onFailure = { error ->
                Log.e("EmailTheme", "Error updating user's email: $error")
            }
        )
    }

    fun resetPassword() {
        userViewModel.sendPasswordResetEmail(email, onSuccess = {
            navController.navigate("sign_in_screen")
        }, onFailure = { error ->
            Log.e("EmailTheme", "Error resetting user's password: $error")
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

