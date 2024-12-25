package com.example.savoreel.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class ThemeViewModel : ViewModel() {
    private val _isDarkModeEnabled = MutableLiveData(false)
    val isDarkModeEnabled: LiveData<Boolean> get() = _isDarkModeEnabled

    fun loadUserSettings() {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val userId = currentUser.uid
        val db = FirebaseFirestore.getInstance()

        viewModelScope.launch {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val darkMode = document.getBoolean("isDarkModeEnabled") ?: false
                        _isDarkModeEnabled.value = darkMode
                        Log.e("DarkMode", "Dark mode setting: $darkMode")
                    } else {
                        _isDarkModeEnabled.value = false
                    }
                }
                .addOnFailureListener { e ->
                    _isDarkModeEnabled.value = false
                    Log.e("ThemeViewModel", "Error loading user settings", e)
                }
        }
    }

    fun resetDarkMode() {
        _isDarkModeEnabled.value = false
    }

    fun toggleDarkMode() {
        _isDarkModeEnabled.value = !_isDarkModeEnabled.value!!

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