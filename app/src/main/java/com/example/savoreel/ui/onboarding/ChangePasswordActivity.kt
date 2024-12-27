package com.example.savoreel.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.savoreel.model.ThemeViewModel
import com.example.savoreel.model.UserViewModel
import com.example.savoreel.ui.component.BackArrow
import com.example.savoreel.ui.component.CustomButton
import com.example.savoreel.ui.component.CustomInputField
import com.example.savoreel.ui.component.CustomTitle
import com.example.savoreel.ui.component.ErrorDialog
import com.example.savoreel.ui.theme.SavoreelTheme

class ChangePasswordActivity: ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        themeViewModel.loadUserSettings()

        setContent {
            val isDarkMode by themeViewModel.isDarkModeEnabled.observeAsState(initial = false)

            SavoreelTheme(darkTheme = isDarkMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ChangePasswordScreen( onChangePasswordSuccess = {
                        val intent = Intent(this, SignInActivity::class.java)
                        startActivity(intent)
                        finish()
                    })
                }
            }
        }
    }
}

@Composable
fun ChangePasswordScreen(onChangePasswordSuccess: () -> Unit) {
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    val userViewModel: UserViewModel = viewModel()

    // Form validity check
    val isFormValid = password.isNotEmpty() && confirmPassword.isNotEmpty() && confirmPassword.length >= password.length

    // Password validation function
    fun isPasswordValid(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }

    fun changePassword() {
        userViewModel.updateUserPassword(password, onSuccess = {
            onChangePasswordSuccess()
        },
            onFailure = { error ->
                errorMessage = error
                showErrorDialog = true
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        BackArrow(
            modifier = Modifier.align(Alignment.TopStart).padding(start = 20.dp, top = 40.dp)
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
                    }
                }
            )

            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // Show error dialog if passwords don't match
    if (showErrorDialog) {
        ErrorDialog(
            title = "Error",
            message = errorMessage,
            onDismiss = { showErrorDialog = false }
        )
    }

    // Show confirmation dialog after successful validation
    if (showConfirmDialog) {
        ErrorDialog(
            title = "Success",
            message = errorMessage,
            onDismiss = {
                showConfirmDialog = false
                changePassword()
            }
        )
    }
}