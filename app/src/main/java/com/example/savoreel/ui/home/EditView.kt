package com.example.savoreel.ui.home

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay


@Composable
fun EditOptionsOverlay(
    onDismiss: () -> Unit,
    options: List<Pair<String, Int>>,
    onSelect: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.secondary),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        options.forEach { (label, iconRes) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(label) }
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = label,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun BlurredInputOverlay(
    label: String,
    initialValue: String,
    onValueChange: (String) -> Unit,
    onDone: () -> Unit,
    maxCharacters: Int
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var text by remember { mutableStateOf(initialValue) }
    val characterCount = maxCharacters - text.length
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.9f))
            .clickable {
                keyboardController?.hide()
                onDone()
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BasicTextField(
                value = text,
                onValueChange = { newValue ->
                    if (newValue.length <= maxCharacters) {
                        text = newValue
                        onValueChange(newValue)
                    }
                },
                singleLine = true,
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                ),
                cursorBrush = SolidColor(Color.White),

                keyboardActions = KeyboardActions(onDone = {
                    keyboardController?.hide()
                    onDone()
                }),

                decorationBox = { innerTextField ->
                    if (text.isEmpty()) {
                        Text(
                            text = label,
                            color = MaterialTheme.colorScheme.onSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                    innerTextField()
                },

                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .background(Color.Transparent)
            )
            Text(
                text = "$characterCount characters remaining",
                color = if (characterCount < 0) MaterialTheme.colorScheme.primary else Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}


@Composable
fun EditableField(
    label: String,
    value: String,
    onStartEdit: () -> Unit,
    ic: Int? = null,
    isTitle: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onStartEdit() }
            .padding(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ic?.let { painterResource(id = it) }?.let {
            Icon(
                painter = it,
                contentDescription = label,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.width(15.dp))
        Text(
            text = value.ifEmpty { label },
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = if(isTitle) FontWeight.Bold else FontWeight.Normal,
            textAlign = if(isTitle) TextAlign.Center else TextAlign.Justify,
            fontSize = if(isTitle) 22.sp else 18.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

fun downloadPhoto(context: Context, photoUri: Uri?) {
    if (photoUri != null) {
        val resolver = context.contentResolver
        val fileName = "Photo_${System.currentTimeMillis()}.jpg"
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }

        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            resolver.openOutputStream(it)?.use { outputStream ->
                resolver.openInputStream(photoUri)?.copyTo(outputStream)
            }
            Toast.makeText(context, "Photo downloaded: $fileName", Toast.LENGTH_SHORT).show()
        }
    } else {
        Toast.makeText(context, "Can not download photo", Toast.LENGTH_SHORT).show()
    }
}

fun sharePhoto(context: Context, photoUri: Uri?) {
    if (photoUri != null) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/jpeg"
            putExtra(Intent.EXTRA_STREAM, photoUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Photo"))
    } else {
        Toast.makeText(context, "No photo to share", Toast.LENGTH_SHORT).show()
    }
}

data class FloatingEmoji(
    val emoji: String,
    val startX: Float,
    val startY: Float
)

@Composable
fun EmojiAnimationDisplay(
    emojiList: MutableList<FloatingEmoji>,
    onAnimationEnd: (FloatingEmoji) -> Unit
) {
    emojiList.forEach { emoji ->
        EmojiAnimation(
            emoji = emoji,
            onAnimationEnd = onAnimationEnd,
            delayMillis = (0..1000).random().toLong()

        )
    }
}

@Composable
fun EmojiAnimation(
    emoji: FloatingEmoji,
    onAnimationEnd: (FloatingEmoji) -> Unit,
    delayMillis: Long = 0
) {
    val animationY = remember { Animatable(emoji.startY) }
    val animationX = remember { Animatable(emoji.startX) }


    LaunchedEffect(Unit) {
        delay(delayMillis)
        animationY.animateTo(
            targetValue = -200f,
            animationSpec = tween(durationMillis = 1500, easing = LinearEasing)
        )
        onAnimationEnd(emoji)
    }

    Text(
        text = emoji.emoji,
        fontSize = 60.sp,
        modifier = Modifier
            .offset(x = animationX.value.dp, y = animationY.value.dp)
    )
}
