package com.example.savoreel

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.example.savoreel.ui.home.TakePhotoActivity
import com.example.savoreel.ui.onboarding.OnboardingActivity
import com.example.savoreel.ui.onboarding.SignInActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val isOnboardingCompleted = sharedPreferences.getBoolean("onboarding_completed", false)

        if (!isOnboardingCompleted) {
            val intent = Intent(this, OnboardingActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            if (FirebaseAuth.getInstance().currentUser == null) {
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this, TakePhotoActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}



