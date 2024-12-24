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
fun NameTheme(
    navController: NavController,
    userViewModel: UserViewModel,
    isChangeName: Boolean,
    onNameSubmitted: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        if(isChangeName) {
            userViewModel.getUser(onSuccess = { user ->
                if (user != null) {
                    name = user.name ?: ""
                } else {
                    Log.e("NameTheme", "User data not found")
                }
            }, onFailure = { error ->
                Log.e("NameTheme", "Error retrieving user: $error")
            })
        }
    }

    fun changeName() {
        userViewModel.updateUserName(name, onSuccess = {
            onNameSubmitted(name)
        }, onFailure = { error ->
            Log.e("NameTheme", "Error updating name: $error")
        })
    }

    Log.e("isChangeName", "$isChangeName")

    CommonForm(
        navController = navController,
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


