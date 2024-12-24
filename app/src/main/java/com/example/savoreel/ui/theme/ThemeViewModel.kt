package com.example.savoreel.ui.theme

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.savoreel.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ThemeViewModel : ViewModel() {
    private val _isDarkModeEnabled = mutableStateOf(false)
    val isDarkModeEnabled: State<Boolean> get() = _isDarkModeEnabled
    private var isSettingsLoaded = false

    fun loadUserSettings() {
        if (isSettingsLoaded) return  // Prevent redundant calls
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            return  // Exit if no user is logged in
        }

        val db = FirebaseFirestore.getInstance()
        val userId = currentUser.uid
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Get the dark mode setting from Firestore, default to false if missing
                    val darkMode = document.getBoolean("isDarkModeEnabled") ?: false
                    _isDarkModeEnabled.value = darkMode
                } else {
                    // Document doesn't exist, default to false
                    _isDarkModeEnabled.value = false
                }
                isSettingsLoaded = true  // Mark as loaded
            }
            .addOnFailureListener {
                // Handle Firestore failure
                _isDarkModeEnabled.value = false
                isSettingsLoaded = true  // Mark as loaded even on failure
            }
    }


    fun resetDarkMode() {
        _isDarkModeEnabled.value = false
    }

    fun toggleDarkMode() {
        _isDarkModeEnabled.value = !_isDarkModeEnabled.value

        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val userId = currentUser.uid

        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userId)
            .update("isDarkModeEnabled", _isDarkModeEnabled.value)
            .addOnSuccessListener {
                Log.d("ThemeViewModel", "Dark mode setting saved to Firestore")
            }
            .addOnFailureListener { e ->
                Log.e("ThemeViewModel", "Error saving dark mode setting: ${e.message}")
            }
    }
}
