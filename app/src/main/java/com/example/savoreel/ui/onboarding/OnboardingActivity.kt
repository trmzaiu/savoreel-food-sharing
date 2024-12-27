package com.example.savoreel.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.savoreel.model.ThemeViewModel
import com.example.savoreel.ui.theme.SavoreelTheme
import com.example.savoreel.ui.theme.nunitoFontFamily
import com.example.savoreel.ui.theme.primaryColor
import com.example.savoreel.ui.theme.secondaryLightColor
import com.example.savoreel.ui.theme.textButtonColor
import kotlinx.coroutines.delay

class OnboardingActivity : ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        themeViewModel.loadUserSettings()

        setContent {
            val isDarkMode by themeViewModel.isDarkModeEnabled.observeAsState(initial = false)

            val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean("onboarding_completed", true)
            editor.apply()

            SavoreelTheme(darkTheme = isDarkMode) {
                OnboardingScreen(
                    navigateToSignIn = {
                        val intent = Intent(this, SignInActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun OnboardingScreen(navigateToSignIn: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { 3 })

    LaunchedEffect(pagerState) {
        val delayTimes = listOf(3000L, 4000L, 3000L)
        while (true) {
            val currentPage = pagerState.currentPage
            val currentDelay = delayTimes[currentPage % delayTimes.size]
            delay(currentDelay)

            val nextPage = (currentPage + 1) % pagerState.pageCount
            pagerState.animateScrollToPage(
                page = nextPage,
                animationSpec = tween(
                    durationMillis = 1000,
                    easing = FastOutSlowInEasing
                )
            )
        }
    }

    Box() {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> Onboarding1Content()
                1 -> Onboarding2Content()
                2 -> Onboarding3Content()
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(primaryColor)
                .height(320.dp)
                .padding(24.dp)
                .align(Alignment.BottomCenter)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                PageIndicator(
                    totalPages = 3,
                    currentPage = pagerState.currentPage,
                )

                Spacer(modifier = Modifier.height(5.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val (title, description) = when (pagerState.currentPage) {
                        0 -> "Capture moments" to "Snap your favorite dishes and\nshare them with your followers."
                        1 -> "Join community" to "Connect with food lovers\nand share your passion!"
                        2 -> "Discover flavors" to "Discover posts featuring hashtags\nthat match your interests."
                        else -> "" to ""
                    }
                    Text(
                        text = title,
                        style = TextStyle(
                            fontSize = 40.sp,
                            lineHeight = 40.sp,
                            fontFamily = nunitoFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = textButtonColor,
                            textAlign = TextAlign.Center
                        )
                    )

                    Text(
                        text = description,
                        style = TextStyle(
                            fontSize = 20.sp,
                            lineHeight = 24.sp,
                            fontFamily = nunitoFontFamily,
                            fontWeight = FontWeight.Normal,
                            color = textButtonColor,
                            textAlign = TextAlign.Center,
                            letterSpacing = 0.5.sp
                        )
                    )
                }


                Spacer(modifier = Modifier.height(25.dp))

                Text(
                    text = "Skip",
                    style = TextStyle(
                        fontSize = 18.sp,
                        lineHeight = 24.sp,
                        fontFamily = nunitoFontFamily,
                        fontWeight = FontWeight.Normal,
                        color = secondaryLightColor,
                        textAlign = TextAlign.Center,
                        letterSpacing = 0.5.sp
                    ),
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { navigateToSignIn() }
                )
            }
        }
    }
}

