package com.example.savoreel.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.savoreel.model.ThemeViewModel
import com.example.savoreel.model.UserViewModel
import com.example.savoreel.ui.component.CommonForm
import com.example.savoreel.ui.component.ErrorDialog
import com.example.savoreel.ui.theme.SavoreelTheme

class PasswordActivity: ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        themeViewModel.loadUserSettings()

        setContent {
            val isDarkMode by themeViewModel.isDarkModeEnabled.observeAsState(initial = false)

            SavoreelTheme(darkTheme = isDarkMode) {
                val isChangePassword = intent.getBooleanExtra("isChangePassword", false)

                PasswordScreen(onPasswordSuccess = {
                    if (isChangePassword) {
                        val intent = Intent(this, ChangePasswordActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        val intent = Intent(this, EmailActivity::class.java).apply {
                            putExtra("isChangeEmail", true)
                        }
                        startActivity(intent)
                        finish()
                    }
                })
            }

        }
    }
}

@Composable
fun PasswordScreen(onPasswordSuccess: () -> Unit) {
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }
    val userViewModel: UserViewModel = viewModel()

    fun validatePassword() {
        userViewModel.validatePassword(password, onSuccess = {
            onPasswordSuccess()
        }, onFailure = { error ->
            errorMessage = "Make sure you entered your password correctly and try again."
            showErrorDialog = true
            Log.e("PasswordTheme", "Error validating password: $error")
        })
    }

    CommonForm(
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