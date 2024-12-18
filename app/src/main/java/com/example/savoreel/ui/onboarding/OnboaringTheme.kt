package com.example.savoreel.ui.onboarding

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
import androidx.compose.foundation.pager.PagerState
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.savoreel.R
import com.example.savoreel.ui.theme.SavoreelTheme
import com.example.savoreel.ui.theme.nunitoFontFamily
import com.example.savoreel.ui.theme.primaryButtonColor
import com.example.savoreel.ui.theme.secondaryLightColor
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.delay

@Composable
fun OnboardingTheme(navController: NavController) {
    val pagerState = rememberPagerState(pageCount = { 3 })

    LaunchedEffect(pagerState) {
        while (true) {
            delay(3000)
            val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
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
                0 -> Onboarding1Content(navController, pagerState)
                1 -> Onboarding2Content(navController)
                2 -> Onboarding3Content(navController)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(primaryButtonColor)
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
                            color = secondaryLightColor,
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
                            color = secondaryLightColor,
                            textAlign = TextAlign.Center,
                            letterSpacing = 0.5.sp
                        )
                    )
                }


                Spacer(modifier = Modifier.height(25.dp))

                Row(horizontalArrangement = Arrangement.SpaceBetween) {
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
                            .padding(end = 16.dp)
                            .clickable { navController.navigate("sign_in_screen") }
                    )
                }
            }
        }
    }
}

@Composable
fun Onboarding1Content(navController: NavController, pagerState: PagerState) {
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
fun Onboarding2Content(navController: NavController) {
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

    LaunchedEffect(Unit) {
        val scrollDistance = 4 * 200f
        val scrollDistance2 = 6 * 200f
        scrollState.animateScrollBy(scrollDistance, animationSpec = tween(durationMillis = 2000))

        delay(500)
        showHeart = true
        triggerAnimation = true

        delay(1000)
        heartVisibility = false

        scrollState.animateScrollBy(scrollDistance2, animationSpec = tween(durationMillis = 2000))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(primaryButtonColor),
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
fun Onboarding3Content(navController: NavController) {
    val selectedIndices = remember { mutableStateListOf<Int>() }

    val indicesToAnimate = listOf(5, 18, 11, 7, 9, 1, 14, 16, 15, 10, 19, 2)

    LaunchedEffect(Unit) {
        delay(3000)
        indicesToAnimate.forEach { index ->
            selectedIndices.add(index)
            delay(200)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(primaryButtonColor),
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
fun Onboarding1Theme(navController: NavController) {
    // Animation state variables
    var foodOffsetX by remember { mutableStateOf((-350).dp) }
    var cardOffsetY by remember { mutableStateOf(500.dp) }
    var showFlash by remember { mutableStateOf(false) }
    var imageSize by remember { mutableStateOf(250.dp) }

    var currentPage by remember { mutableStateOf(0) }
    val totalPages = 3

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

        delay(1000)
        repeat(totalPages - 1) {
            currentPage = (currentPage + 1) % totalPages
            delay(2000)
        }
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
//                PageIndicator(
//                    totalPages = totalPages,
//                    currentPage = currentPage
//                )

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
                        .clickable { navController.navigate("sign_in_screen") }
                )
            }
        }
    }
}

@Composable
fun Onboarding2Theme(navController: NavController) {
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
        targetValue = if (triggerAnimation) 50f else 150f,
        animationSpec = tween(durationMillis = 1500)
    )

    // Animation logic
    LaunchedEffect(Unit) {
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
            .background(primaryButtonColor),
        contentAlignment = Alignment.Center
    ) {
        // Phone frame
        Box(
            modifier = Modifier.offset(y=-135.dp)
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
                    items(boxCount) { index ->
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
                    text = "Join community",
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
                    text = "Connect with food lovers\n and share your passion!",
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
                        .clickable { navController.navigate("sign_in_screen") }
                )
            }
        }
    }
}

@Composable
fun Onboarding3Theme(navController: NavController) {
    val selectedIndices = remember { mutableStateListOf<Int>() }

    val indicesToAnimate = listOf(5, 18, 11, 7, 9, 1, 14, 16, 15, 10, 19, 2)

    LaunchedEffect(Unit) {
        delay(1000)
        indicesToAnimate.forEach { index ->
            selectedIndices.add(index)
            delay(100)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(primaryButtonColor),
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
                    text = "Discover flavors",
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
                    text = "Discover posts featuring hashtags\n that match your interests.",
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
                        .clickable { navController.navigate("sign_in_screen") }
                )
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
                    color = secondaryLightColor
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
                        color = if (isSelected) secondaryLightColor else Color(0xFFBD4343),
                        shape = RoundedCornerShape(4.dp)
                    )

            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun Onboarding1Preview() {
    SavoreelTheme {
        Onboarding1Theme(navController = rememberNavController())
    }
}

@Preview(showBackground = true)
@Composable
fun Onboarding2Preview() {
    SavoreelTheme {
        Onboarding2Theme(navController = rememberNavController())
    }
}

@Preview(showBackground = true)
@Composable
fun Onboarding3Preview() {
    SavoreelTheme {
        Onboarding3Theme(navController = rememberNavController())
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingPreview() {
    SavoreelTheme {
        OnboardingTheme(navController = rememberNavController())
    }
}


