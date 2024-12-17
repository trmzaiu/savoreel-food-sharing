package com.example.savoreel.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.savoreel.R
import com.example.savoreel.ui.theme.SavoreelTheme
import com.example.savoreel.ui.theme.nunitoFontFamily
import com.example.savoreel.ui.theme.primaryButtonColor
import com.example.savoreel.ui.theme.secondaryLightColor
import kotlinx.coroutines.delay

@Composable
fun OnboardingTheme() {
    // Animation state variables
    var foodOffsetX by remember { mutableStateOf((-350).dp) }
    var cardOffsetY by remember { mutableStateOf(500.dp) }
    var showFlash by remember { mutableStateOf(false) }
    var imageSize by remember { mutableStateOf(250.dp) }

    // Launch animation effects
    LaunchedEffect(Unit) {
        // Food appear
        delay(1000)
        foodOffsetX = 0.dp

        // Phone go up
        delay(500)
        cardOffsetY = 80.dp
        delay(100)
        imageSize = 230.dp

        // Take photo
        delay(1000)
        showFlash = true

        delay(200)
        showFlash = false
    }

    // Animating states
    val animatedFoodOffsetX: Dp by animateDpAsState(
        targetValue = foodOffsetX,
        animationSpec = tween(durationMillis = 500)
    )
    val animatedCardOffsetY: Dp by animateDpAsState(
        targetValue = cardOffsetY,
        animationSpec = tween(durationMillis = 1000)
    )
    val animatedImageSize: Dp by animateDpAsState(
        targetValue = imageSize,
        animationSpec = tween(durationMillis = 1000)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(primaryButtonColor),
        contentAlignment = Alignment.Center
    ) {

        Image(
            painter = painterResource(id = R.drawable.image_base),
            contentDescription = "Delicious food",
            modifier = Modifier
                .offset(x = animatedFoodOffsetX, y = -50.dp)
                .size(animatedImageSize)
        )

        Box(
            modifier = Modifier
                .offset(y = animatedCardOffsetY)
                .border(width = 20.dp, color = Color(0xFF741B1B), shape = RoundedCornerShape(size = 35.dp))
                .background(color = Color(0x1AE7CECE), shape = RoundedCornerShape(size = 35.dp))
                .width(318.dp)
                .height(688.57599.dp)
        )

        // Flash effect
        AnimatedVisibility(
            visible = showFlash,
            enter = fadeIn(animationSpec = tween(200)),
            exit = fadeOut(animationSpec = tween(200))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.5f))
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(primaryButtonColor)
                .align(Alignment.BottomCenter)
                .padding(24.dp)
                .height(270.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.slide),
                    contentDescription = "image description",
                    modifier = Modifier.height(8.dp)
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "Capture moments",
                    style = TextStyle(
                        fontSize = 40.sp,
                        lineHeight = 40.sp,
                        fontFamily = nunitoFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = secondaryLightColor,
                        textAlign = TextAlign.Center
                    )
                )
                Text(
                    text = "Snap your favorite dishes and\nshare them with your followers.",
                    style = TextStyle(
                        fontSize = 20.sp,
                        lineHeight = 24.sp,
                        fontFamily = nunitoFontFamily,
                        fontWeight = FontWeight.Normal,
                        color = secondaryLightColor,
                        textAlign = TextAlign.Center,
                        letterSpacing = 0.5.sp
                    )
                )
                Spacer(modifier = Modifier.height(25.dp))

                Text(
                    text = "Skip",
                    style = TextStyle(
                        fontSize = 20.sp,
                        lineHeight = 24.sp,
                        fontFamily = nunitoFontFamily,
                        fontWeight = FontWeight.Normal,
                        color = secondaryLightColor,
                        textAlign = TextAlign.Center,
                        letterSpacing = 0.5.sp
                    ),
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .clickable { /* Add skip action here */ }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingPreview() {
    SavoreelTheme {
        OnboardingTheme()
    }
}
