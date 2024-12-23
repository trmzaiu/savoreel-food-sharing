package com.example.savoreel.ui.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.savoreel.R
import com.example.savoreel.model.Post
import com.example.savoreel.ui.theme.nunitoFontFamily

@Composable
fun CustomInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPasswordField: Boolean = false,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .height(50.dp)
            .width(340.dp)
            .background(color = MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(size = 15.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        singleLine = true,
        visualTransformation = if (isPasswordField) PasswordVisualTransformation() else VisualTransformation.None,
        textStyle = TextStyle(
            fontSize = 16.sp,
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        ),
        decorationBox = { innerTextField ->
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier.fillMaxSize()
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontFamily = nunitoFontFamily,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    )
                }
                innerTextField()
            }
        }
    )
}


@Composable
fun CustomButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .width(360.dp)
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = 18.sp,
                fontFamily = nunitoFontFamily,
                fontWeight = FontWeight.Bold,
                color = if (enabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onTertiary
            )
        )
    }
}

@Composable
fun ErrorDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.scrim)){
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(title, fontFamily = nunitoFontFamily, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground) },
            text = {
                Text(
                    message,
                    style = TextStyle(
                        fontFamily = nunitoFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                    ),
                )},
            containerColor = MaterialTheme.colorScheme.secondary,

            confirmButton = {
                Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Text("Ok", fontFamily = nunitoFontFamily, fontWeight = FontWeight.Normal, color = MaterialTheme.colorScheme.onBackground)
                }
            }
        )
    }
}

@Composable
fun CustomTitle(
    text: String,
    modifier: Modifier = Modifier
){
    Text(
        text = text,
        fontSize = 28.sp,
        lineHeight = 45.sp,
        fontFamily = nunitoFontFamily,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center,
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun BackArrow(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = {
            navController.popBackStack()
        },
        modifier = modifier.size(48.dp),
        content = {
            Icon(
                painter = painterResource(id = R.drawable.chevron_left),
                contentDescription = "Back arrow",
                tint = MaterialTheme.colorScheme.onBackground
            )
        },
    )
}

@Composable
fun ForwardArrow(
    navController: NavController,
    modifier: Modifier = Modifier,
    destination: String
) {
    IconButton(
        onClick = {
            navController.navigate(destination)
        },
        modifier = modifier
            .rotate(180F)
            .size(18.dp),
        content = {
            Icon(
                painter = painterResource(id = R.drawable.chevron_left),
                contentDescription = "Back arrow",
                tint = MaterialTheme.colorScheme.onBackground,
            )
        },
    )
}

@Composable
fun navButton(
    painter: Painter,
    navController: NavController,
    destination: String,
    modifier: Modifier = Modifier,
    isChecked: Boolean = false
) {
    IconButton(
        onClick = {
            navController.navigate(destination)
        },
        modifier = modifier
            .size(48.dp)
            .clip(RoundedCornerShape(50)),
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f)
        )
    ) {
        if (isChecked){
            Image(
                painter = painter,
                contentDescription = null,
                modifier = modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(50)),
            )
        } else {
            Icon(
                painter = painter,
                contentDescription = null,
                modifier = modifier,
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 1f)
            )
        }
    }
}

@Composable
fun IconTheme(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
) {
    Box (
        modifier = Modifier
            .clip(shape = CircleShape)
            .size(40.dp)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)),
        contentAlignment = Alignment.Center
    ){
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = modifier
                .size(20.dp)
                .align(Alignment.Center),
        )
    }
}

@Composable
fun ImageCustom(
    painter: Painter,
    onClick: () -> Unit
){
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(MaterialTheme.shapes.medium)
            .clickable { onClick },
        contentAlignment = Alignment.BottomStart
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            alignment = Alignment.Center,
            )
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
                painter = painterResource(post.imageRes),
                onClick = {}
            )
        }
    }
}

@Composable
fun CustomSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 22.dp else 0.dp,
        animationSpec = tween(durationMillis = 300)
    )

    Box(
        modifier = Modifier
            .size(48.dp, 26.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onCheckedChange(!checked) }
    ) {
        // Track
        Box(
            modifier = Modifier
                .background(
                    color = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,
                    shape = RoundedCornerShape(20.dp)
                )
                .fillMaxSize()
        )

        // Thumb
        Box(
            modifier = Modifier
                .size(26.dp)
                .padding(1.dp)
                .offset(x = thumbOffset)
                .align(Alignment.CenterStart)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.circle),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
            )
        }
    }
}

@Composable
fun PostTopBar(onNavigateTo: NavController) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            navButton(
                painter = painterResource(R.drawable.default_avatar),
                navController = onNavigateTo,
                destination = "",
                isChecked = true
            )
        }
        Row {
            navButton(
                painter = painterResource(id = R.drawable.ic_search),
                navController = onNavigateTo,
                destination = "",
            )
            Spacer(modifier = Modifier.width(16.dp))
            navButton(
                painter = painterResource(id = R.drawable.ic_noti),
                navController = onNavigateTo,
                destination = "",
            )
        }
    }
}