package com.example.savoreel.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ThemeViewModel : ViewModel() {
    private val _isDarkModeEnabled = MutableStateFlow(false)
    val isDarkModeEnabled: StateFlow<Boolean> get() = _isDarkModeEnabled

    /**
     * Load user settings from Firestore
     */
    fun loadUserSettings() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Log.w("ThemeViewModel", "No user logged in")
            _isDarkModeEnabled.value = false
            return
        }

        val userId = currentUser.uid
        val db = FirebaseFirestore.getInstance()

        viewModelScope.launch {
            try {
                val document = db.collection("users").document(userId).get().await()
                val darkMode = document.getBoolean("darkModeEnabled") ?: false
                _isDarkModeEnabled.value = darkMode
                Log.d("ThemeViewModel", "Dark mode loaded: $darkMode")
            } catch (e: Exception) {
                _isDarkModeEnabled.value = false
                Log.e("ThemeViewModel", "Error loading user settings", e)
            }
        }
    }

    /**
     * Toggle dark mode setting and save to Firestore
     */
    fun toggleDarkMode() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Log.w("ThemeViewModel", "No user logged in")
            return
        }

        val userId = currentUser.uid
        val db = FirebaseFirestore.getInstance()

        viewModelScope.launch {
            val newMode = _isDarkModeEnabled.value.not()
            _isDarkModeEnabled.value = newMode

            try {
                db.collection("users").document(userId)
                    .update("darkModeEnabled", newMode)
                    .await()
                Log.d("ThemeViewModel", "Dark mode updated: $newMode")
            } catch (e: Exception) {
                Log.e("ThemeViewModel", "Error saving dark mode setting", e)
            }
        }
    }

    /**
     * Reset dark mode to default (false)
     */
    fun resetDarkMode() {
        _isDarkModeEnabled.value = false
    }
}
