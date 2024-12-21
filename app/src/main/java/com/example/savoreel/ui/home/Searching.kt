package com.example.savoreel.ui.home

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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter.Companion.tint
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.savoreel.R
import com.example.savoreel.ui.component.BackArrow
import com.example.savoreel.ui.component.CustomInputField
import com.example.savoreel.ui.theme.SavoreelTheme

@Composable
fun Searching(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box {
                    BackArrow(
                        navController = navController,
                        modifier = Modifier.align(Alignment.TopStart)
                    )
                }
                CustomInputField(
                    value = "searchQuery",
                    onValueChange = {},
                    placeholder = "Search...",
                    modifier = Modifier
                        .height(50.dp)
                        .padding(top = 15.dp, start = 5.dp, end = 20.dp)
                )

            }
            Column (modifier = Modifier.padding(start = 20.dp, top = 10.dp)) {
                SearchCategory(
                    title = "Recent search",
                    items = listOf(
                        "vietnamese",
                        "vegetarian",
                        "korean",
                        "tiramisu",
                        "fastfood",
                        "bunbo",
                        "buffet",
                        "seafood"
                    ),
                    isSuggestion = false,
                    navController
                )

                SearchCategory(
                    title = "Suggestion for you",
                    items = listOf(
                        "vietnamese",
                        "vegetarian",
                        "korean",
                        "tiramisu",
                        "fastfood",
                        "bunbo",
                        "buffet",
                        "seafood"
                    ),
                    isSuggestion = true,
                    navController
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchCategory(title: String, items: List<String>, isSuggestion: Boolean, navController: NavController) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp),
            color = MaterialTheme.colorScheme.tertiary
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3,
            overflow = FlowRowOverflow.Clip,
            horizontalArrangement = Arrangement.Start,
            verticalArrangement = Arrangement.SpaceBetween

        ) {
            items.forEach { item ->
                SearchItemCard(item = item, isSuggestion = isSuggestion, navController = navController)
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

    }
}

@Composable
fun SearchItemCard(item: String, isSuggestion: Boolean, navController: NavController) {
    Box(
        modifier = Modifier.height(40.dp)
            .clickable {
                navController.navigate("searching_result/Candy")
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondary)
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Image(
                painter = painterResource(if (isSuggestion) R.drawable.ic_increase_red else R.drawable.ic_history),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                colorFilter = tint(if (isSuggestion) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = item,
                color = if (isSuggestion) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    SavoreelTheme(darkTheme = true) {
        Searching(navController = rememberNavController())
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview1() {
    SavoreelTheme(darkTheme = false) {
        Searching(navController = rememberNavController())
    }
}