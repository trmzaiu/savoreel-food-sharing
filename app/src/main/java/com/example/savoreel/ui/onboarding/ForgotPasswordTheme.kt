package com.example.savoreel.ui.onboarding

import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.savoreel.R
import com.example.savoreel.ui.component.CustomTitle
import com.example.savoreel.ui.theme.SavoreelTheme
import com.example.savoreel.ui.theme.backgroundLightColor
import com.example.savoreel.ui.theme.disableButtonColor
import com.example.savoreel.ui.theme.domineFontFamily
import com.example.savoreel.ui.theme.fontDarkColor
import com.example.savoreel.ui.theme.fontLightColor
import com.example.savoreel.ui.theme.lineColor
import com.example.savoreel.ui.theme.nunitoFontFamily
import com.example.savoreel.ui.theme.primaryButtonColor
import com.example.savoreel.ui.theme.secondaryDarkColor
import com.example.savoreel.ui.theme.secondaryLightColor

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
        Image(
            painter = painterResource(id = R.drawable.arrow_left),
            contentDescription = "Back arrow",
            modifier = Modifier
                .padding(20.dp)
                .size(24.dp)
                .align(Alignment.TopStart)
                .clickable {
                    navController.popBackStack()
                }
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            CustomTitle(
                text = "Enter your email"
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Email Input
            BasicTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier
                    .height(50.dp)
                    .width(340.dp)
                    .background(color = secondaryLightColor, shape = RoundedCornerShape(size = 15.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                singleLine = true,
                decorationBox = { innerTextField ->
                    Box(
                        contentAlignment = Alignment.CenterStart,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (email.isEmpty()) {
                            Text(
                                text = "Email",
                                style = TextStyle(
                                    fontSize = 16.sp,
//                                        fontFamily = nunitoFontFamily,
                                    fontWeight = FontWeight.Normal,
                                    color = lineColor
                                )
                            )
                        }
                        innerTextField()
                    }
                }
            )

            Spacer(modifier = Modifier.height(150.dp))

            // Sign In Button
            Button(
                onClick = {
                    if (!validateEmail(email)) {
                        errorMessage = "Make sure you entered your email correctly and try again."
                        showErrorDialog = true
                    } else {
                        println("Email: $email")
                    }
                },
                modifier = Modifier
                    .width(360.dp)
                    .height(50.dp),
                enabled = isFormValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFormValid) primaryButtonColor else disableButtonColor
                )
            ) {
                Text(
                    text = "Sign in",
                    style = TextStyle(
                        fontSize = 18.sp,
//                        fontFamily = nunitoFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = fontDarkColor
                    ))
            }

            if (showErrorDialog) {
                AlertDialog(
                    onDismissRequest = { showErrorDialog = false },
                    title = { Text("Couldn't sign in") },
                    text = { Text(errorMessage) },
                    confirmButton = {
                        Button(onClick = { showErrorDialog = false }) {
                            Text("Ok")
                        }
                    }
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
