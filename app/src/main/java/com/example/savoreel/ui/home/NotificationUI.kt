package com.example.savoreel.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.savoreel.R
import com.example.savoreel.ui.component.BackArrow
import com.example.savoreel.ui.theme.SavoreelTheme


@Composable
fun Notifications(navController: NavController) {
    val notifications = notifications

    Box (modifier = Modifier.background(MaterialTheme.colorScheme.background)){
        Column(modifier = Modifier.padding(top = 40.dp).padding(horizontal = 20.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(50.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                content = {
                    BackArrow(navController = navController,)
                    Text(
                        text = "Notifications",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.headlineMedium,
                    )
                }
            )
            LazyColumn(
            ) {
                notifications.forEach { (date, items) ->
                    item {
                        Text(
                            text = date,
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        )
                    }
                    items(items) { item ->
                        NotificationItem(item)
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationItem(data: NotificationItemData) {
    Row(
        modifier = Modifier
            .fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val avatarResource = when (data.user) {
            "User 1", "User 2" -> R.drawable.ic_facebook
            else -> R.drawable.ic_facebook
        }

//        val avatarResource = user.avatar

        Image(
            painter = painterResource(id = avatarResource),
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(fontFamily = MaterialTheme.typography.titleSmall.fontFamily, fontWeight = MaterialTheme.typography.titleSmall.fontWeight)) {
                    append(data.user)
                }
                withStyle(style = SpanStyle(fontFamily = MaterialTheme.typography.bodyMedium.fontFamily, fontWeight = MaterialTheme.typography.bodyMedium.fontWeight)) {
                    append(" ${data.action}")
                }
            },
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(0.7f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationScreenPreview() {
    SavoreelTheme(darkTheme = false) {
        Notifications(navController = rememberNavController())
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationScreenPreview1() {
    SavoreelTheme(darkTheme = true) {
        Notifications(navController = rememberNavController())
    }
}
data class NotificationItemData(
    val user: String,
    val action: String,
    val timestamp: String
)

val notifications = mapOf(
    "Today" to listOf(
        NotificationItemData("User", "do something blabla like post/comment post/upload post blala bla vlajahdhfhfhfhfhd jjdjdjdjdjdj", "9:41 AM"),
        NotificationItemData("User", "do something blabla like post/comment/upload", "9:41 AM"),
        NotificationItemData("User", "do something blabla like post/comment/upload", "9:41 AM"),
        NotificationItemData("User", "do something blabla like post/comment/upload", "9:41 AM"),
    ),
    "Earlier" to listOf(
        NotificationItemData("User", "do something blabla like post/comment/upload", "9:41 AM"),
        NotificationItemData("User", "do something blabla like post/comment/upload", "9:41 AM"),
        NotificationItemData("User", "do something blabla like post/comment/upload", "9:41 AM"),
        NotificationItemData("User", "do something blabla like post/comment/upload", "9:41 AM"),
        NotificationItemData("User", "do something blabla like post/comment/upload", "9:41 AM"),
    )
)