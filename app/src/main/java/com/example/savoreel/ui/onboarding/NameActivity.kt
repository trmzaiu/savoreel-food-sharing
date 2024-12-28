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
import androidx.compose.runtime.collectAsState
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
class NameActivity: ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        themeViewModel.loadUserSettings()

        setContent {
            val isDarkMode by themeViewModel.isDarkModeEnabled.observeAsState(initial = false)

            SavoreelTheme(darkTheme = isDarkMode) {
                val isChangeName = intent.getBooleanExtra("isChangeName", false)

                NameScreen(isChangeName, onNameSuccess = {
                    if (isChangeName) {
                        val activity = this as? Activity
                        activity?.onBackPressed()
                    } else {
                        val intent = Intent(this, SuccessActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                })
            }
        }
    }
}

@Composable
fun NameScreen(
    isChangeName: Boolean,
    onNameSuccess: (String) -> Unit
) {
    val userViewModel: UserViewModel = viewModel()
    val currentUser by userViewModel.user.collectAsState()
    var name by remember { mutableStateOf("") }

    LaunchedEffect(isChangeName) {
        if (isChangeName) {
            userViewModel.getUser(
                onSuccess = { user ->
                    name = user?.name ?: ""
                    userViewModel.setUser(user)
                },
                onFailure = { error ->
                    Log.e("NameScreen", "Error retrieving user: $error")
                }
            )
        }
    }

    fun changeName() {
        userViewModel.updateUserName(name, onSuccess = {
            onNameSuccess(name)
            userViewModel.setUser(currentUser?.copy(name = name))
        }, onFailure = { error ->
            Log.e("NameTheme", "Error updating name: $error")
        })
    }

    Log.e("isChangeName", "$isChangeName")

    CommonForm(
        title = if (isChangeName) "Change your name" else "What's your name?",
        placeholder = "Name" ,
        buttonText = if (isChangeName) "Save" else "Continue",
        value = name,
        onValueChange = {
            if (it.length <= 15) {
                name = it
            }
        },
        isPasswordField = false,
        isButtonEnabled = name.isNotEmpty(),
        onClickButton = {
            if (name.isNotEmpty()) {
                changeName()
            }
        }
    )
}