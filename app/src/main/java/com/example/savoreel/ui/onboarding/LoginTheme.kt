package com.example.savoreel.ui.onboarding

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.savoreel.R
import com.example.savoreel.ui.theme.backgroundLightColor
import com.example.savoreel.ui.theme.fontDarkColor
import com.example.savoreel.ui.theme.lineColor
import com.example.savoreel.ui.theme.primaryButtonColor
import com.example.savoreel.ui.theme.secondaryDarkColor
import com.example.savoreel.ui.theme.secondaryLightColor

@Composable
fun LoginScreenTheme() {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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
//                style = DomineFont,
                color = primaryButtonColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(100.dp))

            // Username and Password Section
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Username Input
                BasicTextField(
                    value = username,
                    onValueChange = { username = it },
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
                            if (username.isEmpty()) {
                                Text(
                                    text = "Email",
                                    style = TextStyle(
                                        fontSize = 16.sp,
//                                        fontFamily = fontNunitoMedium,
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
//                                        fontFamily = fontNunitoMedium,
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
                        .padding(end = 14.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = "Forgot Password",
                        style = TextStyle(
                            fontSize = 14.sp,
//                            fontFamily = fontNunitoSemibold,
                            color = secondaryDarkColor,
                        ),
                        modifier = Modifier
                            .clickable {
                            println("Forgot Password Clicked")
                        }

                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Login Button
            Button(
                onClick = {
                    // Handle login logic
                    println("Username: $username, Password: $password")
                },
                modifier = Modifier
                    .width(360.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryButtonColor,
                )
            ) {
                Text(
                    text = "Sign in",
                    style = TextStyle(
                        fontSize = 16.sp,
//                        fontFamily = fontNunitoBold,
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
//                    fontFamily = fontNunitoMedium,
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
                                println("Google Login Clicked")
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
                                println("Facebook Login Clicked")
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(42.dp))

            Row(){
                Text(
                    text = "Don't have an account? ",
                    style = TextStyle(
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
//                        fontFamily = fontNunitoRegular,
                        color = secondaryDarkColor,

                        textAlign = TextAlign.Center,
                    )
                )

                Text(
                    text = "Sign Up",
                    style = TextStyle(
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
//                        fontFamily = fontNunitoExtrabold,
                        color = primaryButtonColor,
                        textAlign = TextAlign.Center,
                    )
                )
            }
        }
    }
}
