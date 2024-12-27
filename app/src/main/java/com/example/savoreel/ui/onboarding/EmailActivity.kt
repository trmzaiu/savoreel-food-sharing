package com.example.savoreel.ui.onboarding

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.savoreel.model.ThemeViewModel
import com.example.savoreel.model.UserViewModel
import com.example.savoreel.ui.component.CommonForm
import com.example.savoreel.ui.theme.SavoreelTheme

@Suppress("DEPRECATION")
class EmailActivity: ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        themeViewModel.loadUserSettings()

        setContent {
            val isDarkMode by themeViewModel.isDarkModeEnabled.observeAsState(initial = false)

            SavoreelTheme(darkTheme = isDarkMode) {
                val isChangeEmail = intent.getBooleanExtra("isChangeEmail", false)
                EmailScreen(isChangeEmail, onEmailSuccess = {
                    if (isChangeEmail) {
                        val activity = this as? Activity
                        activity?.onBackPressed()
                    } else {
                        val intent = Intent(this, SignInActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                })
            }
        }
    }
}

@Composable
fun EmailScreen(isChangeEmail: Boolean, onEmailSuccess: () -> Unit) {
    val userViewModel: UserViewModel = viewModel()
    var email by remember { mutableStateOf("") }

    LaunchedEffect(Unit, isChangeEmail) {
        if (isChangeEmail) {
            userViewModel.getUser(onSuccess = { user ->
                if (user != null) {
                    email = user.email.toString()
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
            onEmailSuccess()
        },
            onFailure = { error ->
                Log.e("EmailTheme", "Error updating user's email: $error")
            }
        )
    }

    fun resetPassword() {
        userViewModel.sendPasswordResetEmail(email, onSuccess = {
            onEmailSuccess()
        }, onFailure = { error ->
            Log.e("EmailTheme", "Error resetting user's password: $error")
        })
    }

    CommonForm(
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