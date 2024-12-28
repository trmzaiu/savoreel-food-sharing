package com.example.savoreel.ui.setting

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import com.example.savoreel.model.ThemeViewModel
import com.example.savoreel.ui.component.BackArrow
import com.example.savoreel.ui.theme.SavoreelTheme
import com.example.savoreel.ui.theme.nunitoFontFamily
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup

@Suppress("DEPRECATION")
class TermsActivity: ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        themeViewModel.loadUserSettings()

        setContent {
            val isDarkMode by themeViewModel.isDarkModeEnabled.observeAsState(initial = false)

            SavoreelTheme(darkTheme = isDarkMode) {
                TermsScreen(
                    onAccept = {
                        val activity = this as? Activity
                        activity?.onBackPressed()
                    }
                )
            }
        }
    }
}

@Composable
fun TermsScreen(onAccept: () -> Unit) {
    val termsContent = remember { mutableStateOf("") }
    val isLoading = remember { mutableStateOf(true) }
    var isAccepted by remember { mutableStateOf(false) }

    LaunchedEffect(true) {
        fetchTermsContent(termsContent, isLoading)
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)){
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.background)
            ) {
                BackArrow(
                    modifier = Modifier.align(Alignment.TopStart).padding(start = 20.dp, top = 40.dp)
                )
                Text(
                    text = "Terms & Conditions",
                    fontFamily = nunitoFontFamily,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 32.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 40.dp, bottom = 10.dp)
                )
            }
            LazyColumn(modifier = Modifier.padding(horizontal = 5.dp)) {
                item {
                    Spacer(modifier = Modifier.height(20.dp))

                    if (isLoading.value) {
                        Text(
                            text = "Loading terms...",
                            fontFamily = nunitoFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 20.sp
                        )
                    } else {
                        AndroidView(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            factory = { context ->
                                TextView(context).apply {
                                    text = HtmlCompat.fromHtml(termsContent.value, HtmlCompat.FROM_HTML_MODE_LEGACY)
                                    textSize = 16f
                                    setLineSpacing(1.2f, 1.2f)
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isAccepted,
                                onCheckedChange = { isAccepted = it }
                            )
                            Text(
                                text = "I accept the terms and conditions.",
                                fontSize = 14.sp,
                                fontFamily = nunitoFontFamily,
                                fontWeight = FontWeight.Normal,
                            )
                        }

                        Button(
                            onClick = { onAccept() },
                            enabled = isAccepted,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .fillMaxWidth()
                        ) {
                            Text("Continue")
                        }
                    }
                }
            }
        }
    }
}

private suspend fun fetchTermsContent(termsContent: MutableState<String>, isLoading: MutableState<Boolean>) {
    val client = OkHttpClient()
    val request = Request.Builder().url("https://www.termsfeed.com/live/9fb1fe5f-fd0b-460d-9dc0-817fc0cf0522").build()

    withContext(Dispatchers.IO) {
        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val html = response.body?.string()
                val document = Jsoup.parse(html)

                val content = document.body()?.html() ?: ""

                withContext(Dispatchers.Main) {
                    termsContent.value = content
                    isLoading.value = false
                }
            }
        } catch (e: Exception) {
            Log.e("TermsActivity", "Error fetching policy content", e)
            withContext(Dispatchers.Main) {
                termsContent.value = "Failed to load policy."
                isLoading.value = false
            }
        }
    }
}
