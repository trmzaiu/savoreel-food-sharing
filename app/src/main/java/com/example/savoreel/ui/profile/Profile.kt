package com.example.savoreel.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.savoreel.R
import com.example.savoreel.ui.theme.nunitoFontFamily
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.foundation.layout.Arrangement
import com.example.savoreel.ui.theme.backgroundLightColor

import com.example.savoreel.ui.theme.SavoreelTheme
import com.example.savoreel.ui.theme.primaryButtonColor
import com.example.savoreel.ui.theme.secondaryDarkColor
@Composable
fun Profile(profile: Map<String, List<ProfilePicturesData>>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundLightColor) // Đặt nền màu sáng
            .padding(1.dp) // Đảm bảo có padding bên ngoài để các phần tử không chạm vào mép màn hình
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // Row chứa Avatar, Following và Followers
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Avatar và Tên
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f) // Đảm bảo căn đều với các cột khác
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "User Avatar",
                    tint = Color.Gray,
                    modifier = Modifier.size(100.dp)
                )
                Text (
                    text = "Name",
                    fontFamily = nunitoFontFamily,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                    )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f) // Đảm bảo căn đều với các cột khác
            ) {
                Text(
                    text = "100", // Số lượng Following
                    fontFamily = nunitoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(
                    text = "Following",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }

            // Cột "Followers"
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f) // Đảm bảo căn đều với các cột khác
            ) {
                Text(
                    text = "100", // Số lượng Followers
                    fontFamily = nunitoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(
                    text = "Followers",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // LazyColumn để hiển thị danh sách ProfilePictures
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(2.dp),
        ) {
            profile.forEach { (date, items) ->
                item {
                    Text(
                        text = date,
                        fontFamily = nunitoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .padding(vertical = 4.dp)
                    )
                    Divider(color = Color.LightGray, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(5.dp))

                }
                items(items.chunked(3)) { chunk ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(vertical = 1.dp)
                            .padding(horizontal = 1.dp)
                    ) {
                        ProfilePictures(chunk)
                    }
                }
            }
        }
    }
}

@Composable
fun ProfilePictures(data: List<ProfilePicturesData>) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center, // This centers the items horizontally
        verticalAlignment = Alignment.CenterVertically // Center the items vertically
    ) {
        data.forEach { item -> // Display each item in the chunk
            Image(
                painter = painterResource(id = R.drawable.food), // Replace with your actual icon
                contentDescription = null,
                modifier = Modifier
                    .size(125.dp) // Size of each image
                    .clip(RoundedCornerShape(20.dp)) // Rounded corners
                    .padding(horizontal = 2.dp) // Padding between images
            )
        }
    }
}

data class ProfilePicturesData(
    val title: String,
    val description: String,
    val timestamp: String
)

@Preview
@Composable
fun ProfilePreview() {
    val profile = mapOf(
        "24/2/2024" to listOf(
            ProfilePicturesData("Title", "Description", "9:41 AM"),
            ProfilePicturesData("Title", "Description", "9:41 AM"),
            ProfilePicturesData("Title", "Description", "9:41 AM"),
            ProfilePicturesData("Title", "Description", "9:41 AM"),
            ProfilePicturesData("Title", "Description", "9:41 AM"),
            ProfilePicturesData("Title", "Description", "9:41 AM")
        ),
        "25/2/2024" to listOf(
            ProfilePicturesData("Title", "Description", "9:41 AM"),
            ProfilePicturesData("Title", "Description", "9:41 AM"),
            ProfilePicturesData("Title", "Description", "9:41 AM"),
            ProfilePicturesData("Title", "Description", "9:41 AM"),
            ProfilePicturesData("Title", "Description", "9:41 AM"),
            ProfilePicturesData("Title", "Description", "9:41 AM"),
            ProfilePicturesData("Title", "Description", "9:41 AM"),
            ProfilePicturesData("Title", "Description", "9:41 AM"),
            ProfilePicturesData("Title", "Description", "9:41 AM")
        )
    )

    Profile(profile = profile)
}

