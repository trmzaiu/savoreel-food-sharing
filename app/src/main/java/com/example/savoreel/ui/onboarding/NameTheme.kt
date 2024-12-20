package com.example.savoreel.ui.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.savoreel.ui.component.CommonForm
import com.example.savoreel.ui.theme.SavoreelTheme

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
            if (isCreateMode) {
                navController.navigate("success_screen")
            } else {
                navController.navigate("settings_screen")
            }
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


