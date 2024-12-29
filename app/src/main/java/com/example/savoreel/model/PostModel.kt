package com.example.savoreel.model

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.coroutines.suspendCoroutine

class PostModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _posts = MutableLiveData<List<PostData>>()
    val posts: LiveData<List<PostData>> get() = _posts

    // Initialize Cloudinary in your Application class or MainActivity
    companion object {
        fun initCloudinary(context: Context) {
            val config = HashMap<String, String>()
            config["cloud_name"] = "dnpi98g4e"
            config["api_key"] = "835417185951736"
            config["api_secret"] = "QjLAc8Jzw9MB1-nCJ1_zDr_ivT4"
            MediaManager.init(context, config)
        }
    }

    fun uploadPost(
        name: String,
        title: String?,
        hashtag: String?,
        location: String?,
        photoData: ByteArray,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            onFailure("User not logged in.")
            return
        }
        val userId = currentUser.uid

        if (photoData.isEmpty()) {
            onFailure("Photo data is required.")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Upload to Cloudinary
                val imageUrl = suspendCoroutine<String> { continuation ->
                    val requestId = MediaManager.get()
                        .upload(photoData)
                        .option("folder", "app_uploads")
                        .callback(object : UploadCallback {
                            override fun onStart(requestId: String) {
                                Log.d("Cloudinary", "Upload started")
                            }

                            override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                                val progress = (bytes * 100) / totalBytes
                                Log.d("Cloudinary", "Upload progress: $progress%")
                            }

                            override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                                val imageUrl = resultData["url"] as String
                                continuation.resumeWith(Result.success(imageUrl))
                            }

                            override fun onError(requestId: String, error: ErrorInfo) {
                                continuation.resumeWith(Result.failure(Exception(error.description)))
                            }

                            override fun onReschedule(requestId: String, error: ErrorInfo) {
                                Log.d("Cloudinary", "Upload rescheduled")
                            }
                        })
                        .dispatch()
                }

                withContext(Dispatchers.Main) {
                    val postId = db.collection("posts").document().id

                    val newPost = PostData(
                        postId = postId,
                        userId = userId,
                        name = name,
                        title = title?.takeIf { it.isNotBlank() } ?: "",
                        hashtag = hashtag?.takeIf { it.isNotBlank() } ?: "",
                        location = location?.takeIf { it.isNotBlank() } ?: "",
                        photoUri = imageUrl,
                        date = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date()),
                        reactions = emptyMap()
                    )

                    db.collection("posts")
                        .document(postId)
                        .set(newPost)
                        .addOnSuccessListener {
                            Log.d("UploadPost", "Post saved successfully")
                            onSuccess(postId)
                        }
                        .addOnFailureListener { e ->
                            Log.e("UploadPost", "Error saving post", e)
                            onFailure("Failed to save post: ${e.message}")
                        }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("UploadPost", "Error uploading image", e)
                    onFailure("Failed to upload image: ${e.message}")
                }
            }
        }
    }

    // Original getPostsFromFirebase remains the same
    fun getPostsFromFirebase() {
        db.collection("posts")
            .get()
            .addOnSuccessListener { documents ->
                val fetchedPosts = mutableListOf<PostData>()
                for (document in documents) {
                    val post = document.toObject(PostData::class.java)
                    fetchedPosts.add(post)
                }
                _posts.value = fetchedPosts
            }
            .addOnFailureListener { exception ->
                Log.e("Firebase", "Error fetching posts", exception)
            }
    }
}



