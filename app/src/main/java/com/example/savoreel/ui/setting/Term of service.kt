package com.example.savoreel.ui.setting
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.*
import com.example.savoreel.R
import com.example.savoreel.ui.component.BackArrow
import com.example.savoreel.ui.theme.backgroundLightColor
import com.example.savoreel.ui.theme.disableButtonColor
import com.example.savoreel.ui.theme.nunitoFontFamily
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.example.savoreel.ui.theme.SavoreelTheme
@Composable
fun TermsOfServiceScreen(navController: NavController) {
    var isAccepted by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            BackArrow(
                modifier = Modifier.align(Alignment.CenterStart),
                onClick = { navController.popBackStack() }
            )
            Text(
                text = "Term of service",
                fontFamily = nunitoFontFamily,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                fontSize = 32.sp,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 40.dp)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        LazyColumn(modifier = Modifier.weight(1f))
        {
            item {
                Text(
                    text = buildAnnotatedString {
                        appendBold("Welcome to Savoureel!\n\n")
                        append("We are delighted to introduce Savoureel, a vibrant social network that connects food enthusiasts from around the globe through shared recipes, memorable dining experiences, and a wealth of culinary inspiration. By accessing or using the Savoureel app, website, or any affiliated services (collectively referred to as \"Savoureel\"), you agree to comply with these Terms of Service (\"Terms\"), which form a binding agreement between you and us. If you do not agree to these Terms, please discontinue your use of Savoureel immediately.\n\n")

                        appendBold("1. Eligibility and Account Responsibilities\n")
                        append("To join the Savoureel community, you must:\n")
                        append("• Be at least 13 years of age or meet the legal age requirement in your jurisdiction. If you are under 18, parental consent may be required as per applicable local laws.\n")
                        append("• Create an account using accurate, complete, and up-to-date information.\n")
                        append("As a user, you are responsible for:\n")
                        append("• Maintaining the security and confidentiality of your account credentials.\n")
                        append("• Ensuring that the information you provide remains accurate and updated.\n")
                        append("• Refraining from sharing your login credentials or accessing another user’s account without explicit permission. Any unauthorized use of an account should be reported to us immediately.\n\n")

                        appendBold("2. Content Ownership and Usage\n")
                        append("When you post content on Savoureel—such as recipes, photos, videos, or comments—you retain ownership of your material. However, by uploading or sharing your content on Savoureel, you grant us a non-exclusive, royalty-free, worldwide license to:\n")
                        append("• Use, reproduce, modify, adapt, publish, translate, distribute, and display your content within the platform.\n")
                        append("• Showcase your contributions in promotional materials or other media to highlight user engagement.\n\n")

                        appendBold("3. Community Guidelines\n")
                        append("We aim to cultivate a positive, respectful, and inclusive community. As such, we do not tolerate:\n")
                        append("• Offensive or harmful content, including hate speech, harassment, or abusive language.\n")
                        append("• Content that promotes illegal activities, false information, or violence.\n")
                        append("• The sharing of explicit, obscene, or inappropriate material.\n")
                        append("We reserve the right to review, moderate, and, if necessary, remove content that violates these guidelines. Repeated or severe violations may result in account suspension or permanent termination.\n\n")

                        appendBold("4. Intellectual Property\n")
                        append("All trademarks, logos, and other intellectual property within the Savoureel platform belong to us or our licensors. These materials are protected by copyright, trademark, and other applicable laws. Users are prohibited from:\n")
                        append("• Copying, reproducing, distributing, or modifying any part of Savoureel without prior written authorization.\n")
                        append("• Using our intellectual property for commercial purposes or in a manner that misrepresents affiliation with Savoureel.\n\n")

                        appendBold("5. User Risks and Liability Disclaimer\n")
                        append("While we strive to provide a smooth and enjoyable experience, Savoureel is offered \"as is\" and \"as available.\" We do not guarantee:\n")
                        append("• That the platform will be free from errors, interruptions, or security vulnerabilities.\n")
                        append("• The accuracy, reliability, or safety of third-party content or integrations available within the app.\n")
                        append("You agree that your use of Savoureel is at your own risk. To the extent permitted by law, we disclaim all liability for damages arising from your use of the app, including loss of data, system failures, or third-party disputes.\n\n")

                        appendBold("6. Changes to the Terms of Service\n")
                        append("These Terms may be updated periodically to reflect changes in our services, features, or policies. We will notify users of significant updates through in-app notifications, email, or other reasonable means. Your continued use of Savoureel following such updates constitutes acceptance of the revised Terms.\n\n")

                        appendBold("7. Dispute Resolution and Governing Law\n")
                        append("In the event of a dispute, you agree to resolve it in accordance with the laws of [Jurisdiction], without regard to its conflict-of-law provisions. By using Savoureel, you agree to submit to the exclusive jurisdiction of courts located in [Jurisdiction] for the resolution of any disputes arising from these Terms or the use of the platform.\n\n")

                        append("Contact Us: If you have questions or concerns about these Terms, you can reach out to us at [Support Email Address]. We value your feedback and are here to assist with any inquiries regarding your experience on Savoureel.\n\n")
                        append("Thank you for being part of the Savoureel community. Let’s make food a universal language together!")
                    },
                    fontSize = 20.sp,
                    fontFamily = nunitoFontFamily,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .align(Alignment.CenterHorizontally) // Centers the text horizontally
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

                // Show acknowledgment message after agreeing
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

// Helper function to append bold text
fun AnnotatedString.Builder.appendBold(text: String) {
    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
        append(text)
    }
}

@Preview(showBackground = true)
@Composable
fun TermsOfServiceScreenPreview() {
    TermsOfServiceScreen(navController = rememberNavController())
}
