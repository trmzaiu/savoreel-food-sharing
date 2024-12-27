package com.example.savoreel.ui.setting

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.savoreel.model.ThemeViewModel
import com.example.savoreel.ui.component.BackArrow
import com.example.savoreel.ui.theme.SavoreelTheme
import com.example.savoreel.ui.theme.nunitoFontFamily

class TermsActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeViewModel: ThemeViewModel = viewModel()
            val isDarkMode by themeViewModel.isDarkModeEnabled.observeAsState(initial = false)
            SavoreelTheme(darkTheme = isDarkMode) {
                TermsScreen()
            }
        }
    }
}

@Composable
fun TermsScreen() {
    var isAccepted by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) {
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
                    text = "Terms of service",
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

            LazyColumn(modifier = Modifier.padding(horizontal = 35.dp)) {
                item {
                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Welcome to Savoreel!",
                        fontFamily = nunitoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                    Text(
                        text = buildAnnotatedString {
                            append("We are delighted to introduce Savoreel, a vibrant social network that connects food enthusiasts from around the globe through shared recipes, memorable dining experiences, and a wealth of culinary inspiration. By accessing or using the Savoreel app, website, or any affiliated services (collectively referred to as \"Savoreel\"), you agree to comply with these Terms of Service (\"Terms\"), which form a binding agreement between you and us. If you do not agree to these Terms, please discontinue your use of Savoreel immediately.\n\n")

                            appendBold("1. Eligibility and Account Responsibilities\n")
                            append("To join the Savoreel community, you must:\n")
                            append("    • Be at least 13 years of age or meet the legal age requirement in your jurisdiction. If you are under 18, parental consent may be required as per applicable local laws.\n")
                            append("    • Create an account using accurate, complete, and up-to-date information.\n")
                            append("As a user, you are responsible for:\n")
                            append("    • Maintaining the security and confidentiality of your account credentials.\n")
                            append("    • Ensuring that the information you provide remains accurate and updated.\n")
                            append("    • Refraining from sharing your login credentials or accessing another user’s account without explicit permission. Any unauthorized use of an account should be reported to us immediately.\n\n")

                            appendBold("2. Content Ownership and Usage\n")
                            append("When you post content on Savoreel—such as recipes, photos, videos, or comments—you retain ownership of your material. However, by uploading or sharing your content on Savoreel, you grant us a non-exclusive, royalty-free, worldwide license to:\n")
                            append("    • Use, reproduce, modify, adapt, publish, translate, distribute, and display your content within the platform.\n")
                            append("    • Showcase your contributions in promotional materials or other media to highlight user engagement.\n\n")

                            appendBold("3. Community Guidelines\n")
                            append("We aim to cultivate a positive, respectful, and inclusive community. As such, we do not tolerate:\n")
                            append("    • Offensive or harmful content, including hate speech, harassment, or abusive language.\n")
                            append("    • Content that promotes illegal activities, false information, or violence.\n")
                            append("    • The sharing of explicit, obscene, or inappropriate material.\n")
                            append("We reserve the right to review, moderate, and, if necessary, remove content that violates these guidelines. Repeated or severe violations may result in account suspension or permanent termination.\n\n")

                            appendBold("4. Intellectual Property\n")
                            append("All trademarks, logos, and other intellectual property within the Savoreel platform belong to us or our licensors. These materials are protected by copyright, trademark, and other applicable laws. Users are prohibited from:\n")
                            append("    • Copying, reproducing, distributing, or modifying any part of Savoreel without prior written authorization.\n")
                            append("    • Using our intellectual property for commercial purposes or in a manner that misrepresents affiliation with Savoreel.\n\n")

                            appendBold("5. User Risks and Liability Disclaimer\n")
                            append("While we strive to provide a smooth and enjoyable experience, Savoreel is offered \"as is\" and \"as available.\" We do not guarantee:\n")
                            append("    • That the platform will be free from errors, interruptions, or security vulnerabilities.\n")
                            append("    • The accuracy, reliability, or safety of third-party content or integrations available within the app.\n")
                            append("You agree that your use of Savoreel is at your own risk. To the extent permitted by law, we disclaim all liability for damages arising from your use of the app, including loss of data, system failures, or third-party disputes.\n\n")

                            appendBold("6. Changes to the Terms of Service\n")
                            append("These Terms may be updated periodically to reflect changes in our services, features, or policies. We will notify users of significant updates through in-app notifications, email, or other reasonable means. Your continued use of Savoreel following such updates constitutes acceptance of the revised Terms.\n\n")

                            appendBold("7. Dispute Resolution and Governing Law\n")
                            append("In the event of a dispute, you agree to resolve it in accordance with the laws of [Jurisdiction], without regard to its conflict-of-law provisions. By using Savoreel, you agree to submit to the exclusive jurisdiction of courts located in [Jurisdiction] for the resolution of any disputes arising from these Terms or the use of the platform.\n\n")

                            append("Contact Us: If you have questions or concerns about these Terms, you can reach out to us at [Support Email Address]. We value your feedback and are here to assist with any inquiries regarding your experience on Savoreel.\n\n")
                            append("Thank you for being part of the Savoreel community. Let’s make food a universal language together!")
                        },
                        fontSize = 14.sp,
                        fontFamily = nunitoFontFamily,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .align(Alignment.CenterHorizontally), // Centers the text horizontally
                        textAlign = TextAlign.Start
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Acknowledge Button
                    Button(
                        onClick = { isAccepted = true },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .fillMaxWidth()
                    ) {
                        Text("I Agree", fontSize = 18.sp)
                    }

                    if (isAccepted) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "You have accepted the Terms of Service.",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}



// Helper function to append bold text
fun AnnotatedString.Builder.appendBold(text: String) {
    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
        append(text)
    }
}