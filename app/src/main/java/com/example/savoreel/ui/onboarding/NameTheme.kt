package com.example.savoreel.ui.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.example.savoreel.model.UserViewModel
import com.example.savoreel.ui.component.CommonForm

@Composable
fun NameTheme(
    navController: NavController,
    userViewModel: UserViewModel,
    userId: Int,
    isChangeName: Boolean,
    onNameSubmitted: (String) -> Unit
) {
    val user = userViewModel.findUserById(userId)
    var name by remember { mutableStateOf(user?.name ?: "") }

    CommonForm(
        navController = navController,
        title = if (isChangeName) "What's your name?" else "Change your name",
        placeholder = "Name" ,
        buttonText = if (isChangeName) "Continue" else "Save",
        value = name,
        onValueChange = { name = it },
        isPasswordField = false,
        isButtonEnabled = name.isNotEmpty(),
        onClickButton = {
            onNameSubmitted(name)
        }
    )
}

//@Preview(showBackground = true)
//@Composable
//fun UpdateNameDarkPreview() {
//    SavoreelTheme(darkTheme = true) {
//        NameTheme(navController = rememberNavController(), isChangeName = false, currentName = "Tra My Vu") { name ->
//            println("Changed name to: $name")
//        }
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun CreateNameDarkPreview() {
//    SavoreelTheme(darkTheme = true) {
//        NameTheme(navController = rememberNavController(), isChangeName = true, currentName = "") { name ->
//            println("Created name: $name")
//        }
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun UpdateNameLightPreview() {
//    SavoreelTheme (darkTheme = false) {
//        NameTheme(navController = rememberNavController(), isChangeName = false, currentName = "Tra My Vu") { name ->
//            println("Changed name to: $name")
//        }
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun CreateNameLightPreview() {
//    SavoreelTheme(darkTheme = false) {
//        NameTheme(navController = rememberNavController(), isChangeName = true, currentName = "") { name ->
//            println("Created name: $name")
//        }
//    }
//}


