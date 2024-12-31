@file:Suppress("DEPRECATION")

package com.example.savoreel.ui.onboarding

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.savoreel.R
import com.example.savoreel.model.UserViewModel
import com.example.savoreel.ui.component.CustomButton
import com.example.savoreel.ui.component.CustomInputField
import com.example.savoreel.ui.component.ErrorDialog
import com.example.savoreel.ui.home.TakePhotoActivity
import com.example.savoreel.ui.theme.SavoreelTheme
import com.example.savoreel.ui.theme.domineFontFamily
import com.example.savoreel.ui.theme.nunitoFontFamily
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class SignInActivity : ComponentActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        userViewModel.signInResult.observe(this) { result ->
            when (result) {
                UserViewModel.SignInResult.NewUser -> navigateToSuccessActivity()
                UserViewModel.SignInResult.ExistingUser -> navigateToTakePhotoActivity()
            }
        }

        setContent {
            SavoreelTheme(darkTheme = false) {
                SignInScreen(
                    onSignInSuccess = {
                        val intent = Intent(this, TakePhotoActivity::class.java)
                        startActivity(intent)
                        finish()
                    },
                    onForgotPassword = {
                        val intent = Intent(this, EmailActivity::class.java).apply {
                            putExtra("isChangeEmail", false)
                        }
                        startActivity(intent)
                    },
                    onGoogleSignIn = { signInWithGoogle() },
                    onFacebookSignIn = {},
                    navigateToSignUp = {
                        val intent = Intent(this, SignUpActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }

    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                val account = task.getResult(ApiException::class.java)
                account?.let { userViewModel.signInWithGoogle(it) }
            }
        }

    private fun signInWithGoogle() {
        googleSignInClient.signOut().addOnCompleteListener {
            Log.d("SignInActivity", "Google account signed out. Starting new sign-in flow.")
            val signInIntent = googleSignInClient.signInIntent
            googleSignInLauncher.launch(signInIntent)
        }
    }

    private fun navigateToTakePhotoActivity() {
        val intent = Intent(this, TakePhotoActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToSuccessActivity() {
        val intent = Intent(this, SuccessActivity::class.java)
        startActivity(intent)
        finish()
    }
}

@Composable
fun SignInScreen(
    onSignInSuccess: () -> Unit,
    onForgotPassword: () -> Unit,
    onGoogleSignIn: () -> Unit,
    onFacebookSignIn: () -> Unit,
    navigateToSignUp: () -> Unit,
) {
    val userViewModel: UserViewModel = viewModel()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val isFormValid = email.isNotEmpty() && password.isNotEmpty()

    fun signIn() {
        isLoading = true
        userViewModel.signIn(email, password, onSuccess = {
            isLoading = false
            onSignInSuccess()
        }, onFailure = {
            isLoading = false
            errorMessage = "Make sure you entered your email and password correctly and try again."
            showErrorDialog = true
            Log.e("SignInScreen", errorMessage)
        })
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(20.dp)
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Image(
                painter = painterResource(R.drawable.rounded_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 24.dp)
            )

            Text(
                text = "Savoreel",
                fontSize = 48.sp,
                lineHeight = 20.sp,
                fontFamily = domineFontFamily,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(100.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                CustomInputField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Email",
                    isPasswordField = false
                )

                CustomInputField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Password",
                    isPasswordField = true
                )

                Box(
                    modifier = Modifier
                        .padding(end = 20.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = "Forgot Password",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = nunitoFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSecondary,
                        ),
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onForgotPassword()
                            }

                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            CustomButton(
                text = if (isLoading) "Loading..." else "Sign in",
                enabled = isFormValid,
                onClick = { signIn() }
            )

            Spacer(modifier = Modifier.height(70.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Or connect with",
                    fontSize = 16.sp,
                    fontFamily = nunitoFontFamily,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 20.sp,
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_google),
                        contentDescription = "Google Icon",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onGoogleSignIn()
                            }
                    )

                    Spacer(modifier = Modifier.width(16.dp))
//
                    // Facebook Icon
                    Image(
                        painter = painterResource(id = R.drawable.ic_facebook),
                        contentDescription = "Facebook Icon",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onFacebookSignIn()
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(42.dp))

            Row {
                Text(
                    text = "Don't have an account? ",
                    style = TextStyle(
                        fontSize = 15.sp,
                        lineHeight = 20.sp,
                        fontFamily = nunitoFontFamily,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSecondary,

                        textAlign = TextAlign.Center,
                    )
                )

                Text(
                    text = "Sign Up",
                    style = TextStyle(
                        fontSize = 15.sp,
                        lineHeight = 20.sp,
                        fontFamily = nunitoFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                    ),
                    modifier = Modifier
                        .clickable (
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            navigateToSignUp()
                        }
                )
            }
        }
    }

    if (showErrorDialog) {
        ErrorDialog(
            title = "Couldn't sign in",
            message = errorMessage,
            onDismiss = { showErrorDialog = false }
        )
    }
}

