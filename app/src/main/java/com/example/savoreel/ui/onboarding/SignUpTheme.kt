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
import androidx.compose.foundation.layout.wrapContentWidth
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
import com.example.savoreel.ui.component.CustomButton
import com.example.savoreel.ui.component.CustomInputField
import com.example.savoreel.ui.component.ErrorDialog
import com.example.savoreel.ui.theme.SavoreelTheme
import com.example.savoreel.ui.theme.backgroundLightColor
import com.example.savoreel.ui.theme.disableButtonColor
import com.example.savoreel.ui.theme.domineFontFamily
import com.example.savoreel.ui.theme.fontDarkColor
import com.example.savoreel.ui.theme.lineColor
import com.example.savoreel.ui.theme.linkColor
import com.example.savoreel.ui.theme.nunitoFontFamily
import com.example.savoreel.ui.theme.primaryButtonColor
import com.example.savoreel.ui.theme.secondaryDarkColor
import com.example.savoreel.ui.theme.secondaryLightColor

@Composable
fun SignUpScreenTheme(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }

    val existEmail = "example@gmail.com"

    val isFormValid = email.isNotEmpty() && password.isNotEmpty()

    fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun existedEmail(email: String): Boolean {
        return email == existEmail
    }

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
                lineHeight = 20.sp,
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
                //Email Input
                CustomInputField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Email",
                    isPasswordField = false
                )

                // Password Input
                CustomInputField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Password",
                    isPasswordField = true
                )

                // Noti
                Box(
                    modifier = Modifier
                        .padding(start = 20.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "Password must at least 8 characters",
                        style = TextStyle(
                            fontSize = 14.sp,
//                            fontFamily = nunitoFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            color = secondaryDarkColor,
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Sign Up Button
            CustomButton(
                text = "Sign up",
                enabled = isFormValid,
                onClick = {
                    if (!isEmailValid(email)) {
                        errorMessage = "Make sure you entered email format correctly and try again."
                        showErrorDialog = true
                    } else if (existedEmail(email)) {
                        errorMessage = "Email existed, please try another email."
                        showErrorDialog = true
                    } else if (password.length < 8) {
                        errorMessage = "Password must at least 8 characters."
                        showErrorDialog = true
                    } else {
                        println("Email: $email, Password: $password")
                    }
                }
            )

            Spacer(modifier = Modifier.height(15.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "By continuing you agree to our ",
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Normal,
                    color = linkColor,
                    textAlign = TextAlign.Center,
                )
                Row(
                    modifier = Modifier.wrapContentWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Terms of Service",
                        fontSize = 16.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = primaryButtonColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.clickable { println("Move to read Terms of Service") }
                    )
                    Text(
                        text = " and ",
                        fontSize = 16.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Normal,
                        color = linkColor,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = "Privacy Policy",
                        fontSize = 16.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = primaryButtonColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.clickable { println("Move to read Privacy Policy") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(111.dp))

            Row(){
                Text(
                    text = "Have an account? ",
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
                    text = "Sign In",
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
                            navController.navigate("sign_in_screen")
                        }
                )
            }

            if (showErrorDialog) {
                ErrorDialog(
                    title = "Couldn't sign up",
                    message = errorMessage,
                    onDismiss = { showErrorDialog = false }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpPreview() {
    SavoreelTheme {
        SignUpScreenTheme(navController = rememberNavController())
    }
}
