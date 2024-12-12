package com.example.savoreel.ui.onboarding

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.savoreel.ui.component.BackArrow
import com.example.savoreel.ui.component.CustomButton
import com.example.savoreel.ui.component.CustomInputField
import com.example.savoreel.ui.component.CustomTitle
import com.example.savoreel.ui.component.ErrorDialog
import com.example.savoreel.ui.theme.SavoreelTheme
import com.example.savoreel.ui.theme.backgroundLightColor

@Composable
fun ForgotPasswordTheme(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }

    val validEmail = "example@gmail.com"

    fun validateEmail(email: String): Boolean {
        return email == validEmail
    }

    fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    val isFormValid = email.isNotEmpty() && isEmailValid(email)


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundLightColor),
        contentAlignment = Alignment.Center
    ) {
        BackArrow(
            modifier = Modifier
                .align(Alignment.TopStart),
            onClick = {
                navController.popBackStack()
            }
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(20.dp)
        ) {
            CustomTitle(
                text = "Enter your email"
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Email Input
            CustomInputField(
                value = email,
                onValueChange = { email = it },
                placeholder = "Email",
                isPasswordField = false
            )

            Spacer(modifier = Modifier.height(162.dp))

            // Button
            CustomButton(
                text = "Continue",
                enabled = isFormValid,
                onClick = {
                    if (!validateEmail(email)) {
                        errorMessage = "Make sure you entered your email correctly and try again."
                        showErrorDialog = true
                    } else {
                        navController.navigate("verify_code_screen")
                    }
                }
            )

            Spacer(modifier = Modifier.height(80.dp))

            if (showErrorDialog) {
                ErrorDialog(
                    title = "Couldn't sign in",
                    message = errorMessage,
                    onDismiss = { showErrorDialog = false }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordPreview() {
    SavoreelTheme {
        ForgotPasswordTheme(navController = rememberNavController())
    }
}
