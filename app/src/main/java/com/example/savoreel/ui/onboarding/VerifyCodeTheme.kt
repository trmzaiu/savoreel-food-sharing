package com.example.savoreel.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.savoreel.ui.component.BackArrow
import com.example.savoreel.ui.component.CustomButton
import com.example.savoreel.ui.component.CustomTitle
import com.example.savoreel.ui.component.ErrorDialog
import com.example.savoreel.ui.theme.SavoreelTheme
import com.example.savoreel.ui.theme.lineColor

@Composable
fun VerifyCodeTheme(navController: NavController) {
    var code by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }

    val isCodeValid = code.length == 4 && code.all { it.isDigit() }

    val validCode = "1234"

    fun validateCode(code: String): Boolean {
        return code == validCode
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        BackArrow(
            modifier = Modifier
                .align(Alignment.TopStart),
            onClick = {
                navController.popBackStack()
            }
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(20.dp)
        ) {
            CustomTitle(
                text = "Enter verification code"
            )

            Spacer(modifier = Modifier.height(15.dp))

            // Code Input
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(4) { index ->
                    BasicTextField(
                        value = code.getOrNull(index)?.toString() ?: "",
                        onValueChange = {
                            if (it.length <= 1 && it.all { char -> char.isDigit() }) {
                                val newCode = StringBuilder(code)
                                if (code.length > index) newCode[index] = it[0] else newCode.append(it)
                                code = newCode.toString().take(4)
                            }
                        },
                        textStyle = TextStyle(
                            fontSize = 32.sp,
                            lineHeight = 24.sp,
                            //fontFamily = nunitoFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center,
                        ),
                        modifier = Modifier
                            .border(width = 1.dp, color = lineColor, shape = RoundedCornerShape(size = 12.dp))
                            .width(56.dp)
                            .height(56.dp)
                            .background(color = MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(size = 12.dp))
                            .padding(8.dp)
                            .align(Alignment.CenterVertically)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(){
                Text(
                    text = "Didn't get the code? ",
                    style = TextStyle(
                        fontSize = 15.sp,
                        lineHeight = 20.sp,
//                        fontFamily = nunitoFontFamily,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.outline,
                        textAlign = TextAlign.Center,
                    )
                )

                Text(
                    text = "Resend Code",
                    style = TextStyle(
                        fontSize = 15.sp,
                        lineHeight = 20.sp,
//                        fontFamily = nunitoFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.tertiary,
                        textAlign = TextAlign.Center,
                    ),
                    modifier = Modifier
                        .clickable {
                            navController.navigate("sign_in_screen")
                        }
                )
            }

            Spacer(modifier = Modifier.height(123.dp))

            // Button
            CustomButton(
                text = "Verify",
                enabled = isCodeValid,
                onClick = {
                    if (!validateCode(code)) {
                        errorMessage = "Verification code is incorrect. Please try again."
                        showErrorDialog = true
                    } else {
                        navController.navigate("reset_password_screen")
                    }
                }
            )

            Spacer(modifier = Modifier.height(92.dp))

            if (showErrorDialog) {
                ErrorDialog(
                    title = "Unverifiable",
                    message = errorMessage,
                    onDismiss = { showErrorDialog = false }
                )
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun VerifyCodeDarkPreview() {
    SavoreelTheme(darkTheme = true) {
        VerifyCodeTheme(navController = rememberNavController())
    }
}

@Preview(showBackground = true)
@Composable
fun VerifyCodeLightPreview() {
    SavoreelTheme(darkTheme = false) {
        VerifyCodeTheme(navController = rememberNavController())
    }
}
