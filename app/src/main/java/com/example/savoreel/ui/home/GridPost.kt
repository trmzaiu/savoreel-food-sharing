package com.example.savoreel.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.savoreel.model.postss
import com.example.savoreel.ui.component.GridImage
import com.example.savoreel.ui.component.PostTopBar
import com.example.savoreel.ui.theme.SavoreelTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GridPost(navController: NavController) {
    var selectedOption by remember { mutableStateOf("Everyone") }
    val options = listOf("Everyone", "Follower")
    var expanded by remember { mutableStateOf(false) }

    Box (
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ){
        Column{
            PostTopBar(navController)
            GridImage(postss, {})
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GridPostScreenPreview() {
    SavoreelTheme(darkTheme = false) {
        GridPost(navController = rememberNavController())
    }
}

@Preview(showBackground = true)
@Composable
fun GridPostScreenPreview1() {
    SavoreelTheme(darkTheme = true) {
        GridPost(navController = rememberNavController())
    }
}