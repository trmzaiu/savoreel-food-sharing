package com.example.savoreel.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.savoreel.R
import com.example.savoreel.ui.component.CustomButton
import com.example.savoreel.ui.theme.SavoreelTheme
import com.example.savoreel.ui.theme.nunitoFontFamily
import com.example.savoreel.ui.theme.secondaryLightColor
import com.example.savoreel.ui.theme.textButtonColor
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.delay

@Composable
fun HashTagTheme(navController: NavController) {
    var selectedTags by remember { mutableStateOf<MutableSet<String>>(mutableSetOf()) }
    var visibleHashtags by remember { mutableStateOf<List<String>>(emptyList()) }
    val allHashtags = listOf(
        "#fastfood", "#vietnamese", "#korean", "#vegetarian",
        "#sushi", "#dessert", "#other", "#cake", "#chinese",
        "#hotpot", "#cookie", "#pizza", "#burgers", "#pasta",
        "#salad", "#steak", "#seafood", "#noodles", "#tacos",
        "#soup", "#grill"
    )

    LaunchedEffect(Unit) {
        allHashtags.forEachIndexed { index, tag ->
            delay(100)
            visibleHashtags = allHashtags.take(index + 1)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = Modifier
                .shadow(elevation = 5.dp, spotColor = Color(0x80000000), ambientColor = Color(0x40000000), shape = RoundedCornerShape(size = 30.dp))
                .width(350.dp)
                .height(750.dp)

                .background(
                    color = secondaryLightColor,
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
                    .size(120.dp)
                    .padding(bottom = 24.dp)
            )

            Text(
                text = "What kinds of\n food do you like?",
                style = TextStyle(
                    fontSize = 32.sp,
                    lineHeight = 30.sp,
                    fontFamily = nunitoFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                )
            )

            Spacer(modifier = Modifier.height(30.dp))

            Box(
                modifier = Modifier
                    .width(300.dp)
                    .height(260.dp)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                FlowRow(
                    mainAxisSpacing = 10.dp,
                    crossAxisSpacing = 10.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    visibleHashtags.forEach { tag ->
                        HashtagItem(
                            text = tag,
                            isSelected = selectedTags.contains(tag),
                            onClick = {
                                selectedTags = if (selectedTags.contains(tag)) {
                                    selectedTags.toMutableSet().apply { remove(tag) }
                                } else {
                                    selectedTags.toMutableSet().apply { add(tag) }
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            CustomButton(
                text = "Continue",
                enabled = selectedTags.isNotEmpty(),
                onClick = {
                    println("Selected hashtags: ${selectedTags.joinToString(", ")}")
                    navController.navigate("takephoto_screen")
                },
                modifier = Modifier
                    .width(240.dp)
                    .height(50.dp),
            )

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = "Skip",
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 16.sp,
                    fontFamily = nunitoFontFamily,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSecondary,
                ),
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    navController.navigate("takephoto_screen")
                }
            )

        }
    }
}

@Composable
fun HashtagItem(
    text: String,
    isSelected: Boolean,
    onClick: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .shadow(elevation = 4.dp, spotColor = Color(0x80000000), ambientColor = Color(0x40000000), shape = RoundedCornerShape(size = 8.dp))
            .background(
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                shape = RoundedCornerShape(size = 8.dp)
            )
            .clickable {
                onClick(text)
            }
            .padding(horizontal = 12.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = 16.sp,
                fontFamily = nunitoFontFamily,
                fontWeight = FontWeight.SemiBold,
                color = if (isSelected) textButtonColor else MaterialTheme.colorScheme.onBackground
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HashTagPreview() {
    SavoreelTheme() {
        HashTagTheme(navController = rememberNavController())
    }
}