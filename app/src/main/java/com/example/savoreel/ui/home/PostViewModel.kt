package com.example.savoreel.ui.home

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class PostViewModel : ViewModel() {
    private val _currentState = mutableStateOf(PhotoState.TakePhoto)
    val currentState : State<PhotoState> = _currentState

    private val _isCapturing = mutableStateOf(false)
    val isCapturing: State<Boolean> = _isCapturing

    private val _isCaptureLocked = mutableStateOf(false)
    val isCaptureLocked: State<Boolean> = _isCaptureLocked

    // Photo states
    private val _photoUri = mutableStateOf<Uri?>(null)
    val photoUri: State<Uri?> get() = _photoUri

    private val _isPhotoTaken = mutableStateOf(false)
    val isPhotoTaken: State<Boolean> get() = _isPhotoTaken

    private val _title = mutableStateOf("")
    val title: State<String> = _title

    private val _location = mutableStateOf("")
    val location: State<String> = _location

    private val _hashtag = mutableStateOf("")
    val hashtag: State<String> = _hashtag

    private val _editingField = mutableStateOf<String?>(null) // Which field is being edited
    val editingField: State<String?> = _editingField

    private val _isFrontCamera = mutableStateOf(false)
    val isFrontCamera: State<Boolean> = _isFrontCamera

    private val _flashEnabled = mutableStateOf(false)
    val flashEnabled: State<Boolean> = _flashEnabled

    private val _option = mutableStateOf<List<Pair<String, Int>>>(emptyList())
    val option: State<List<Pair<String, Int>>> = _option

    fun navigateToState(state: PhotoState) {
        _currentState.value = state
    }

    fun setOption(option: List<Pair<String, Int>>) {
        _option.value = option
    }

    fun setPhotoUri(uri: Uri?) {
        _photoUri.value = uri
    }

    fun setisPhotoTaken(value: Boolean) {
        _isPhotoTaken.value = value
    }

    fun setisFrontCamera(value: Boolean) {
        _isFrontCamera.value = value
    }

    fun setisFlashEnabled(value: Boolean) {
        _flashEnabled.value = value
    }
    
    fun setisCaptureLocked(value: Boolean) {
        _isCaptureLocked.value = value
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

