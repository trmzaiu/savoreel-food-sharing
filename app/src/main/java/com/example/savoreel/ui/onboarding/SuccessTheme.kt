package com.example.savoreel.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.savoreel.R
import com.example.savoreel.ui.theme.SavoreelTheme
import com.example.savoreel.ui.theme.nunitoFontFamily
import com.example.savoreel.ui.theme.secondaryLightColor


@Composable
fun SuccessTheme(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = Modifier
                .shadow(elevation = 4.dp, spotColor = Color(0x80000000), ambientColor = Color(0x40000000), shape = RoundedCornerShape(size = 30.dp))
                .width(350.dp)
                .height(750.dp)
                .background(
                    color = MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(size = 30.dp)
                )
        )

        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(R.drawable.rounded_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(150.dp)
                    .padding(bottom = 24.dp)
            )

            Text(
                text = "Success!",
                style = TextStyle(
                    fontSize = 40.sp,
                    lineHeight = 20.sp,
                    fontFamily = nunitoFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary,
                    textAlign = TextAlign.Center,
                )
            )

            Text(
                text = "Your account has been created",
                style = TextStyle(
                    fontSize = 20.sp,
                    lineHeight = 20.sp,
                    fontFamily = nunitoFontFamily,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.tertiary,
                    textAlign = TextAlign.Center,
                )
            )

            Spacer(modifier = Modifier.height(120.dp))

            Button(
                modifier = Modifier
                    .width(240.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                onClick = { navController.navigate("hashtag_screen")}
            ) {
                Text(
                    text = "Continue",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontFamily = nunitoFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = secondaryLightColor
                    )
                )
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun SuccessDarkPreview() {
    SavoreelTheme(darkTheme = true) {
        SuccessTheme(navController = rememberNavController())
    }
}

@Preview(showBackground = true)
@Composable
fun SuccessLightPreview() {
    SavoreelTheme(darkTheme = false) {
        SuccessTheme(navController = rememberNavController())
    }
}