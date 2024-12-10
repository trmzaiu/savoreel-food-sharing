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
import com.example.savoreel.ui.theme.SavoreelTheme
import com.example.savoreel.ui.theme.backgroundLightColor
import com.example.savoreel.ui.theme.disableButtonColor
import com.example.savoreel.ui.theme.domineFontFamily
import com.example.savoreel.ui.theme.fontDarkColor
import com.example.savoreel.ui.theme.lineColor
import com.example.savoreel.ui.theme.nunitoFontFamily
import com.example.savoreel.ui.theme.primaryButtonColor
import com.example.savoreel.ui.theme.secondaryDarkColor
import com.example.savoreel.ui.theme.secondaryLightColor

@Composable
fun SignInScreenTheme(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }

    val validEmail = "example@gmail.com"
    val validPassword = "123456"

    fun validateLogin(email: String, password: String): Boolean {
        return email == validEmail && password == validPassword
    }

    val isFormValid = email.isNotEmpty() && password.isNotEmpty()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundLightColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Image(
                painter = painterResource(R.drawable.rounded_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 24.dp)
            )

            Text(
                text = "Savoreel",
                fontSize = 48.sp,
                lineHeight = 19.5.sp,
//                fontFamily = domineFontFamily,
                fontWeight = FontWeight.Bold,
                color = primaryButtonColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(100.dp))

            // Email and Password Section
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
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

                // Password Input
                BasicTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier
                        .height(50.dp)
                        .width(340.dp)
                        .background(color = secondaryLightColor, shape = RoundedCornerShape(size = 15.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    decorationBox = { innerTextField ->
                        Box(
                            contentAlignment = Alignment.CenterStart,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            if (password.isEmpty()) {
                                Text(
                                    text = "Password",
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

                // Forgot Password
                Box(
                    modifier = Modifier
                        .padding(end = 20.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = "Forgot Password",
                        style = TextStyle(
                            fontSize = 14.sp,
//                            fontFamily = nunitoFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            color = secondaryDarkColor,
                        ),
                        modifier = Modifier
                            .clickable {
                            println("Move to ForgotPasswordTheme")
                        }

                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Sign In Button
            Button(
                onClick = {
                    if (!validateLogin(email, password)) {
                        errorMessage = "Make sure you entered your email and password correctly and try again."
                        showErrorDialog = true
                    } else {
                        println("Email: $email, Password: $password")
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

            Spacer(modifier = Modifier.height(70.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Or connect with",
                    fontSize = 16.sp,
//                    fontFamily = nunitoFontFamily,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 19.5.sp,
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                ) {
                    // Google Icon
                    Image(
                        painter = painterResource(id = R.drawable.ic_google),
                        contentDescription = "Google Icon",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                println("Sign in with google")
                            }
                    )

                    Spacer(modifier = Modifier.width(16.dp))
//
                    // Facebook Icon
                    Image(
                        painter = painterResource(id = R.drawable.ic_facebook),
                        contentDescription = "Facebook Icon",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                println("Sign in with facebook")
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(42.dp))

            Row(){
                Text(
                    text = "Don't have an account? ",
                    style = TextStyle(
                        fontSize = 15.sp,
                        lineHeight = 20.sp,
//                        fontFamily = nunitoFontFamily,
                        fontWeight = FontWeight.Normal,
                        color = secondaryDarkColor,

                        textAlign = TextAlign.Center,
                    )
                )

                Text(
                    text = "Sign Up",
                    style = TextStyle(
                        fontSize = 15.sp,
                        lineHeight = 20.sp,
//                        fontFamily = nunitoFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        color = primaryButtonColor,
                        textAlign = TextAlign.Center,
                    ),
                    modifier = Modifier
                        .clickable {
                            navController.navigate("signup_screen")
                        }
                )

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
}

@Preview(showBackground = true)
@Composable
fun SignInPreview() {
    SavoreelTheme {
        SignInScreenTheme(navController = rememberNavController())
    }
}
