package com.example.savoreel.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowOverflow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.savoreel.R
import com.example.savoreel.ui.component.BackArrow
import com.example.savoreel.ui.component.navButton
import com.example.savoreel.ui.home.Post
import com.example.savoreel.ui.home.posts
import com.example.savoreel.ui.theme.SavoreelTheme
import com.example.savoreel.ui.theme.nunitoFontFamily

@Composable
fun ProfileScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .padding(top = 40.dp)
                .padding(horizontal = 20.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                content = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ){
                        navButton(
                            painter = painterResource(R.drawable.default_avatar),
                            destination = "",
                            navController = navController,
                            isChecked = true
                        )
                        Text(
                            text = "Giang Hoang",
                            fontFamily = nunitoFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                    Row(){
                        navButton(
                            painter = painterResource(R.drawable.ic_setting),
                            navController = navController,
                            destination = "setting",
                        )

                        BackArrow(
                            navController = navController,
                            modifier = Modifier
                                .padding(0.dp)
                                .size(48.dp)
                                .rotate(180f)
                        )
                    }
                }
            )
            LazyColumn {
                item{
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Avatar và Tên
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Image(
                                painter = painterResource(R.drawable.default_avatar),
                                contentDescription = "User Avatar",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(RoundedCornerShape(50))
                            )
                            Text(
                                text = "Tra Giang Hoang",
                                fontFamily = nunitoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    // move qa following
                                }
                        ) {
                            Text(
                                text = "100",
                                fontFamily = nunitoFontFamily,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                            Text(
                                text = "Following",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    // move qa following
                                }
                        ) {
                            Text(
                                text = "100",
                                fontFamily = nunitoFontFamily,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                            Text(
                                text = "Followers",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CalendarWithImages(
    posts: List<Post>,
    modifier: Modifier = Modifier
) {
    val dayList = MutableList<Painter>(30) { painterResource(R.drawable.ic_add) }

    Column(modifier = modifier) {
        Box(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = "December, 2024",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            overflow = FlowRowOverflow.Clip,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {

            for (i in 1..30) {
                posts.forEach { post ->
                    if (post.date == i) {
                        dayList[i - 1] = painterResource(post.imageRes)
                    }
                }
            }
            dayList.forEach { day ->
                Image(
                    painter = day,
                    contentDescription = null,
                    modifier = Modifier.size(50.dp)
                )
            }
        }
    }
}



@Composable
fun MyCalendarApp() {
    val images = listOf(
        painterResource(R.drawable.food), // Ngày 1
        painterResource(R.drawable.food), // Ngày 2
        painterResource(R.drawable.food), // Ngày 3
        painterResource(R.drawable.food), // Ngày 1
        painterResource(R.drawable.food), // Ngày 2
        painterResource(R.drawable.food), // Ngày 3
        painterResource(R.drawable.food), // Ngày 1
        painterResource(R.drawable.food), // Ngày 2
        painterResource(R.drawable.food), // Ngày 3
        painterResource(R.drawable.food), // Ngày 1
        painterResource(R.drawable.food), // Ngày 2
        painterResource(R.drawable.food), // Ngày 3
        painterResource(R.drawable.food), // Ngày 1
        painterResource(R.drawable.food), // Ngày 2
        painterResource(R.drawable.food), // Ngày 3
        painterResource(R.drawable.food), // Ngày 1
        painterResource(R.drawable.food), // Ngày 2
        painterResource(R.drawable.food), // Ngày 3
        // Thêm các ảnh tương ứng cho ngày tiếp theo
    )

    CalendarWithImages(posts)
}


@Preview(showBackground = true)
@Composable
fun ProfilePreview() {
//    ProfileScreen(
//        navController = rememberNavController()
//    )
    SavoreelTheme {
        MyCalendarApp()
    }
}