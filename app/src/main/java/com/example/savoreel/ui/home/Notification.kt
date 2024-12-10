package com.example.savoreel.ui.home

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

@Composable
fun Notifications(notifications: Map<String, List<NotificationItemData>>) {

    Column {
        Spacer(modifier = Modifier.height(60.dp))

        Text(
            text = "Notifications",
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 32.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            notifications.forEach { (date, items) ->
                item {
                    Text(
                        text = date,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                    Divider(color = Color.LightGray, thickness = 1.dp)
                }
                items(items) { item ->
                    NotificationItem(item)
                }
            }
        }
    }
}

@Composable
fun NotificationItem(data: NotificationItemData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(
                color = Color(0xFFF5F5F5),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_facebook), // Replace with your icon
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(4.dp))
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = data.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = data.description,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Timestamp
        Text(
            text = data.timestamp,
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

data class NotificationItemData(
    val title: String,
    val description: String,
    val timestamp: String
)

@Preview
@Composable
fun NotificationScreenPreview() {
    val notifications = mapOf(
        "24/2/2024" to listOf(
            NotificationItemData("Title", "Description", "9:41 AM"),
            NotificationItemData("Title", "Description", "9:41 AM"),
            NotificationItemData("Title", "Description", "9:41 AM")
        ),
        "25/2/2024" to listOf(
            NotificationItemData("Title", "Description", "9:41 AM"),
            NotificationItemData("Title", "Description", "9:41 AM"),
            NotificationItemData("Title", "Description", "9:41 AM")
        )
    )

    Notifications(notifications = notifications)
}
