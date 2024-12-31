package com.example.savoreel.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.savoreel.R
import com.example.savoreel.model.ThemeViewModel
import com.example.savoreel.ui.theme.SavoreelTheme
import com.example.savoreel.ui.theme.nunitoFontFamily
import com.example.savoreel.ui.theme.primaryColor
import com.example.savoreel.ui.theme.secondaryLightColor
import com.example.savoreel.ui.theme.textButtonColor
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.delay

class OnboardingActivity : ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        themeViewModel.loadUserSettings()

        setContent {
            val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean("onboarding_completed", true)
            editor.apply()

            SavoreelTheme(darkTheme =  false) {
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


@Composable
fun Onboarding1Content() {
    // Animation state variables
    var foodOffsetX by remember { mutableStateOf((-400).dp) }
    var cardOffsetY by remember { mutableStateOf(500.dp) }
    var showFlash by remember { mutableStateOf(false) }
    var imageSize by remember { mutableStateOf(250.dp) }

    LaunchedEffect(Unit) {
        delay(1000)
        foodOffsetX = 0.dp
        delay(500)
        cardOffsetY = 80.dp
        delay(100)
        imageSize = 230.dp
        delay(1000)
        showFlash = true
        delay(200)
        showFlash = false
    }

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
            .background(primaryColor),
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
    }
}

@Composable
fun Onboarding2Content() {
    val boxCount = 10
    val scrollState = rememberLazyListState()
    var showHeart by remember { mutableStateOf(false) }
    var heartVisibility by remember { mutableStateOf(true) }
    var triggerAnimation by remember { mutableStateOf(false) }

    val heartAlpha by animateFloatAsState(
        targetValue = if (triggerAnimation) 0f else 1f,
        animationSpec = tween(durationMillis = 1000)
    )
    val heartOffsetY by animateFloatAsState(
        targetValue = if (triggerAnimation) 75f else 175f,
        animationSpec = tween(durationMillis = 1500)
    )

    LaunchedEffect(scrollState) {
        triggerAnimation = false
        showHeart = false
        heartVisibility = true

        scrollState.scrollToItem(0)

        val scrollDistance = 4 * 200f
        scrollState.animateScrollBy(scrollDistance, animationSpec = tween(durationMillis = 2000))

        delay(500)
        showHeart = true
        triggerAnimation = true

        delay(1000)
        heartVisibility = false

        val remainingDistance = (boxCount - 4) * 200f
        scrollState.animateScrollBy(remainingDistance, animationSpec = tween(durationMillis = 2000))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(primaryColor),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.offset(y = -135.dp)
        ) {
            Box(
                modifier = Modifier
                    .border(width = 20.dp, color = Color(0xFF741B1B), shape = RoundedCornerShape(size = 35.dp))
                    .width(250.dp)
                    .height(400.dp)
                    .background(color = Color(0x1AE7CECE), shape = RoundedCornerShape(size = 35.dp))
                    .padding(start = 35.dp, top = 45.dp, end = 35.dp)
            ) {
                LazyColumn(
                    state = scrollState,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(boxCount) {
                        Box(
                            modifier = Modifier
                                .width(180.dp)
                                .height(180.dp)
                                .background(
                                    color = Color(0xFFBD4343),
                                    shape = RoundedCornerShape(15.dp)
                                )
                        )
                    }
                }
                if (showHeart && heartVisibility) {
                    Icon(
                        painter = painterResource(id = R.drawable.heart),
                        contentDescription = "Heart",
                        tint = Color.Red,
                        modifier = Modifier
                            .size(30.dp)
                            .offset(y = heartOffsetY.dp, x = 100.dp)
                            .alpha(heartAlpha)
                    )
                }
            }
        }
    }
}

@Composable
fun Onboarding3Content() {
    val selectedIndices = remember { mutableStateListOf<Int>() }

    val indicesToAnimate = listOf(5, 18, 11, 7, 9, 1, 14, 16, 15, 10, 19, 2)

    LaunchedEffect(Unit) {
        delay(2500)
        indicesToAnimate.forEach { index ->
            selectedIndices.add(index)
            delay(300)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(primaryColor),
        contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier.offset(y = -135.dp)) {
            Box(
                modifier = Modifier
                    .border(width = 20.dp, color = Color(0xFF741B1B), shape = RoundedCornerShape(size = 35.dp))
                    .width(250.dp)
                    .height(400.dp)
                    .background(color = Color(0x1AE7CECE), shape = RoundedCornerShape(size = 35.dp))
                    .padding(start = 40.dp, end = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                val hashtags = listOf(
                    "fastfood", "vietnamese", "korean", "vegetarian",
                    "sushi", "dessert", "other", "cake", "chinese",
                    "hotpot", "cookie", "pizza", "burgers", "pasta",
                    "salad", "steak", "seafood", "noodles", "tacos",
                    "soup", "vietnamese"
                )

                FlowRow(
                    mainAxisSpacing = 8.dp,
                    crossAxisSpacing = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    hashtags.forEachIndexed { index, tag ->
                        val isSelected = selectedIndices.contains(index)
                        Hashtag(
                            text = tag,
                            isSelected = isSelected
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Hashtag(
    text: String,
    isSelected: Boolean = false
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFFBD9C9C) else Color(0xFFBD4343),
        animationSpec = tween(durationMillis = 300)
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFFBD9C9C) else Color(0xFFBD4343),
        animationSpec = tween(durationMillis = 300)
    )

    Box(
        modifier = Modifier
            .background(
                backgroundColor,
                shape = RoundedCornerShape(size = 8.dp)
            )
            .padding(horizontal = 6.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(){
            Text(
                text = "#",
                style = TextStyle(
                    fontSize = 10.sp,
                    fontFamily = nunitoFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    color = textButtonColor
                )
            )
            Text(
                text = text,
                style = TextStyle(
                    fontSize = 10.sp,
                    fontFamily = nunitoFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    color = textColor
                )
            )
        }
    }
}

@Composable
fun PageIndicator(
    totalPages: Int,
    currentPage: Int,

    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        repeat(totalPages) { page ->
            val isSelected = page == currentPage
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .height(8.dp)
                    .width(if (isSelected) 24.dp else 12.dp)
                    .background(
                        color = if (isSelected) textButtonColor else Color(0xFFBD4343),
                        shape = RoundedCornerShape(4.dp)
                    )

            )
        }
    }
}


