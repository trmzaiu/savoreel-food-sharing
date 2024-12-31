package com.example.savoreel.model

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ThemeViewModel : ViewModel() {
    private val _isDarkModeEnabled = MutableStateFlow(false)
    val isDarkModeEnabled: StateFlow<Boolean> get() = _isDarkModeEnabled

    fun loadUserSettings() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Log.w("ThemeViewModel", "No user logged in")
            return
        }

        val userId = currentUser.uid
        val db = FirebaseFirestore.getInstance()

        // Use addOnSuccessListener and addOnFailureListener instead of coroutines
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val darkMode = document.getBoolean("darkModeEnabled") ?: false
                _isDarkModeEnabled.value = darkMode
                Log.d("ThemeViewModel", "Dark mode loaded: $darkMode")
            }
            .addOnFailureListener { e ->
                _isDarkModeEnabled.value = false
                Log.e("ThemeViewModel", "Error loading user settings", e)
            }
    }

    fun loadDarkModeFromState(): Boolean {
        return _isDarkModeEnabled.value
    }

    fun toggleDarkMode() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Log.w("ThemeViewModel", "No user logged in")
            return
        }

        val userId = currentUser.uid
        val db = FirebaseFirestore.getInstance()

        val newMode = _isDarkModeEnabled.value.not()
        _isDarkModeEnabled.value = newMode

        // Use addOnSuccessListener and addOnFailureListener for Firestore update
        db.collection("users").document(userId)
            .update("darkModeEnabled", newMode)
            .addOnSuccessListener {
                Log.d("ThemeViewModel", "Dark mode updated: $newMode")
            }
            .addOnFailureListener { e ->
                Log.e("ThemeViewModel", "Error saving dark mode setting", e)
            }
    }

    fun resetDarkMode() {
        _isDarkModeEnabled.value = false
    }
}
