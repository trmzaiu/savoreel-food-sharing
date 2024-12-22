package com.example.savoreel.ui.home

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.savoreel.R
import com.example.savoreel.ui.theme.SavoreelTheme
import kotlinx.coroutines.launch

@Composable
fun EditOptionsOverlay(
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    val options = listOf(
        "Hashtag" to R.drawable.ic_avar,
        "Location" to R.drawable.ic_edit,
        "Download" to R.drawable.ic_noti,
        "Share" to R.drawable.ic_send
    )
    Box(
        modifier = Modifier
            .background(Color.Black.copy(alpha = 0.9f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            options.forEach { (label, iconRes) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(label) }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = label,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(15.dp))
                    Text(
                        text = label,
                        modifier = Modifier.weight(1f)
                    )
                }
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
                onValueChange = {
                    text = it
                    onValueChange(it)
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
                            color = Color.Gray,
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
                color = Color.Gray,
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
    textAlign: TextAlign = TextAlign.Center,
    fontWeight: FontWeight = FontWeight.Bold,
    ic: Int? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onStartEdit() }
            .padding(8.dp),
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
            fontWeight = fontWeight,
            color = Color.Black,
            textAlign = textAlign,
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun EditPreview() {

    SavoreelTheme() {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val scope = rememberCoroutineScope()
        ModalBottomSheet(
            onDismissRequest = { scope.launch { sheetState.hide() } },
            sheetState = sheetState
        ) {
            EditOptionsOverlay(
                onDismiss = { scope.launch { sheetState.hide() } },
                onSelect = {}
            )
        }
    }
}

