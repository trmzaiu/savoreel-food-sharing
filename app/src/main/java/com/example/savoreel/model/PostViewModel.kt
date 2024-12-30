package com.example.savoreel.model

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.savoreel.ui.home.PhotoState
import com.example.savoreel.ui.home.SheetContent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PostViewModel : ViewModel() {
    private val _currentState = mutableStateOf(PhotoState.None)
    val currentState : State<PhotoState> = _currentState

    private val _isFrontCamera = MutableStateFlow(true)
    val isFrontCamera: StateFlow<Boolean> = _isFrontCamera

    private val _flashEnabled = MutableStateFlow(false)
    val flashEnabled: StateFlow<Boolean> = _flashEnabled

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

    private val _option = mutableStateOf<List<Pair<String, Int>>>(emptyList())
    val option: State<List<Pair<String, Int>>> = _option

    private val _currentSheetContent = mutableStateOf(SheetContent.NONE)
    val currentSheetContent: State<SheetContent> = _currentSheetContent

    private val _selectedEmoji = mutableStateOf<String?>(null)
    val selectedEmoji: State<String?> = _selectedEmoji

    private val _postID = mutableStateOf("")
    val postID: State<String> = _postID

    fun setPostID(id: String) {
        _postID.value = id
    }
    fun setcurrentSheetContent(state: SheetContent) {
        _currentSheetContent.value = state
    }

    fun setSelectedEmoji(emoji: String?) {
        _selectedEmoji.value = emoji
    }

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

    fun setFrontCamera(isFront: Boolean) {
        _isFrontCamera.value = isFront
    }

    fun setFlashEnabled(enabled: Boolean) {
        _flashEnabled.value = enabled
    }

    fun resetPhoto() {
        _photoUri.value = null
        _isPhotoTaken.value = false
        _title.value = ""
        _location.value = ""
        _hashtag.value = ""
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

