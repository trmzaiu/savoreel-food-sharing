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
import com.example.savoreel.ui.component.CommonForm
import com.example.savoreel.ui.component.CustomButton
import com.example.savoreel.ui.component.CustomInputField
import com.example.savoreel.ui.component.CustomTitle
import com.example.savoreel.ui.component.ErrorDialog
import com.example.savoreel.ui.theme.SavoreelTheme
import com.example.savoreel.ui.theme.backgroundLightColor

@Composable
fun NameTheme(
    navController: NavController,
    currentName: String?,
    onNameSubmitted: (String) -> Unit
) {
    var name by remember { mutableStateOf(currentName ?: "") }

    val isCreateMode = currentName.isNullOrEmpty()

    CommonForm(
        navController = navController,
        title = if (isCreateMode) "What's your name?" else "Change your name",
        placeholder = "Name" ,
        buttonText = if (isCreateMode) "Continue" else "Save",
        value = name,
        onValueChange = { name = it },
        isPasswordField = false,
        isButtonEnabled = name.isNotEmpty(),
        onClickButton = {
            onNameSubmitted(name)
        }
    )
}

@Preview(showBackground = true)
@Composable
fun UpdateNameDarkPreview() {
    SavoreelTheme(darkTheme = true) {
        NameTheme(navController = rememberNavController(), currentName = "Tra My Vu") { name ->
            println("Changed name to: $name")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateNameDarkPreview() {
    SavoreelTheme(darkTheme = true) {
        NameTheme(navController = rememberNavController(), currentName = null) { name ->
            println("Created name: $name")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UpdateNameLightPreview() {
    SavoreelTheme (darkTheme = false) {
        NameTheme(navController = rememberNavController(), currentName = "Tra My Vu") { name ->
            println("Changed name to: $name")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateNameLightPreview() {
    SavoreelTheme(darkTheme = false) {
        NameTheme(navController = rememberNavController(), currentName = null) { name ->
            println("Created name: $name")
        }
    }
}


