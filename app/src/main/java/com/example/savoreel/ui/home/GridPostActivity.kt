package com.example.savoreel.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.savoreel.model.Post
import com.example.savoreel.model.ThemeViewModel
import com.example.savoreel.model.UserViewModel
import com.example.savoreel.model.postss
import com.example.savoreel.ui.component.ImageCustom
import com.example.savoreel.ui.component.PostTopBar
import com.example.savoreel.ui.profile.FollowActivity
import com.example.savoreel.ui.profile.UserAvatar
import com.example.savoreel.ui.theme.SavoreelTheme
import com.example.savoreel.ui.theme.nunitoFontFamily

class GridPostActivity: ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        themeViewModel.loadUserSettings()

        setContent {
            val isDarkMode by themeViewModel.isDarkModeEnabled.observeAsState(initial = false)
            val userID = intent.getStringExtra("USER_ID") ?: "Everyone"
            SavoreelTheme(darkTheme = isDarkMode) {
                GridPost(userID = userID)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GridPost(userID: String) {
    val userViewModel: UserViewModel = viewModel()
    var isRowVisible by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf("") }
    var uid by remember { mutableStateOf("") }
    var numberOfFollower by remember { mutableIntStateOf(0) }
    var numberOfFollowing by remember { mutableIntStateOf(0) }

    isRowVisible = userID != "Everyone"

    LaunchedEffect(Unit) {
        userViewModel.getUserById(
            userId = userID,
            onSuccess = { user ->
                if (user != null) {
                    name = user.name.toString()
                    uid = user.userId.toString()
                    numberOfFollower = user.followers.size
                    numberOfFollowing = user.following.size
                } else {
                    Log.e("GridPost", "User data not found")
                }
            },
            onFailure = { error ->
                Log.e("GridPost", "Error retrieving user: $error")
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        Column {
            PostTopBar()

            if (isRowVisible) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val context = LocalContext.current

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        UserAvatar(
                            userId = uid,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(50))
                        )
                        Text(
                            text = name,
                            fontFamily = nunitoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(top = 5.dp)
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                val intent = Intent(context, FollowActivity::class.java).apply {
                                    putExtra("USER_ID", userID)
                                    putExtra("TAB", "following")
                                }
                                context.startActivity(intent)
                            }
                    ) {
                        Text(
                            text = numberOfFollowing.toString(),
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
                                val intent = Intent(context, FollowActivity::class.java).apply {
                                    putExtra("USER_ID", userID)
                                    putExtra("TAB", "follower")
                                }
                                context.startActivity(intent)
                            }
                    ) {
                        Text(
                            text = numberOfFollower.toString(),
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

            GridImage(postss, {})
        }
    }
}

@Composable
fun GridImage(posts: List<Post>, onClick: (Post) -> Unit, modifier: Modifier = Modifier){
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 100.dp),
        contentPadding = PaddingValues(5.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = modifier
    ) {
        items(posts) { post ->
            ImageCustom(
                url = post.imageRes,
                onClick = {
                    // doi chanh
                }
            )
        }
    }
}