package com.example.savoreel.ui.home

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import android.net.Uri

class PostViewModel : ViewModel() {
    private val _isCapturing = mutableStateOf(false)
    val isCapturing: State<Boolean> = _isCapturing

    private val _isCaptureLocked = mutableStateOf(false)
    val isCaptureLocked: State<Boolean> = _isCaptureLocked

    // Photo states
    private val _photoUri = mutableStateOf<Uri?>(null)
    val photoUri: State<Uri?> = _photoUri

    private val _isPhotoTaken = mutableStateOf(false)
    val isPhotoTaken: State<Boolean> = _isPhotoTaken

    // Fields
    private val _title = mutableStateOf("")
    val title: State<String> = _title

    private val _location = mutableStateOf("")
    val location: State<String> = _location

    private val _hashtag = mutableStateOf("")
    val hashtag: State<String> = _hashtag

    private val _editingField = mutableStateOf<String?>(null) // Which field is being edited
    val editingField: State<String?> = _editingField

    fun setPhotoUri(uri: Uri?) {
        _photoUri.value = uri
        _isPhotoTaken.value = uri != null
        _isCaptureLocked.value = uri != null
    }

    fun setisPhotoTaken(value: Boolean) {
        _isPhotoTaken.value = value
    }
    
    fun setisCapturing(value: Boolean) {
        _isCapturing.value = value
    }
    
    fun resetPhoto() {
        _photoUri.value = null
        _isPhotoTaken.value = false
        _isCapturing.value = false
        _title.value = ""
        _location.value = ""
        _hashtag.value = ""
        _isCaptureLocked.value = false
    }

    fun startEditingField(field: String) {
        _editingField.value = field
    }

    fun stopEditingField() {
        _editingField.value = null
    }

    fun updateFieldValue(field: String, value: String) {
        when (field) {
            "title" -> _title.value = value
            "location" -> _location.value = value
            "hashtag" -> _hashtag.value = value
        }
    }
}

