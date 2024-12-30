package com.example.savoreel.model

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.coroutines.suspendCoroutine

class PostModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> get() = _posts

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

        val hashtagsList = hashtag?.split(" ")?.map { it.trim() }?.filter { it.startsWith("#") } ?: emptyList()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Upload to Cloudinary
                val imageUrl = suspendCoroutine { continuation ->
                    MediaManager.get()
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

                    val newPost = Post(
                        postId = postId,
                        userId = userId,
                        name = name,
                        title = title.toString(),
                        hashtag = hashtagsList,
                        location = location.toString(),
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

    fun uploadEmojiReaction(
        postId: String,
        emoji: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val postRef = db.collection("posts").document(postId)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(postRef)

            if (!snapshot.exists()) {
                throw Exception("Post not found")
            }

            val currentReactions = snapshot.get("reactions") as? Map<String, Long> ?: emptyMap()
            val updatedReactions = currentReactions.toMutableMap()

            val currentCount = updatedReactions[emoji] ?: 0
            updatedReactions[emoji] = currentCount + 1

            transaction.update(postRef, "reactions", updatedReactions)
        }.addOnSuccessListener {
            Log.d("Firebase", "Emoji reaction added successfully")
            onSuccess()
        }.addOnFailureListener { exception ->
            Log.e("Firebase", "Failed to add emoji reaction", exception)
            onFailure("Failed to add emoji reaction: ${exception.localizedMessage}")
        }
    }

    // Original getPostsFromFirebase remains the same
    fun getPostsFromFirebase() {
        db.collection("posts")
            .get()
            .addOnSuccessListener { documents ->
                val fetchedPosts = mutableListOf<Post>()
                for (document in documents) {
                    val post = document.toObject(Post::class.java)
                    fetchedPosts.add(post)
                }
                _posts.value = fetchedPosts
            }
            .addOnFailureListener { exception ->
                Log.e("Firebase", "Error fetching posts", exception)
            }
    }

    private fun getCurrentUserId(): String? {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return null
        return currentUser.uid
    }

    fun getFollowingUserIds() {
        val currentUserId = getCurrentUserId() ?: return
        db.collection("users").document(currentUserId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val followingIds = document.get("following") as? List<String> ?: emptyList()
                    // Handle case where user is not following anyone
                    if (followingIds.isEmpty()) {
                        Log.d("Firebase", "User is not following anyone.")
                        getPostsFromUsers(currentUserId, followingIds)
                    } else {
                        // Fetch posts for the current user and their following list
                        getPostsFromUsers(currentUserId, followingIds)
                    }
                } else {
                    Log.e("Firebase", "User document not found")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firebase", "Error fetching following list", exception)
            }
    }

    private fun getPostsFromUsers(currentUserId: String, followingIds: List<String>) {
        val userIdsToFetch = followingIds.toMutableList().apply { add(currentUserId) }

        if (userIdsToFetch.size > 10) {
            val chunkedLists = userIdsToFetch.chunked(10)
            chunkedLists.forEach { chunk ->
                fetchPostsForChunk(chunk)
            }
        } else {
            fetchPostsForChunk(userIdsToFetch)
        }
    }

    private fun fetchPostsForChunk(userIds: List<String>) {
        db.collection("posts")
            .whereIn("userId", userIds)
            .get()
            .addOnSuccessListener { documents ->
                val fetchedPosts = mutableListOf<Post>()
                for (document in documents) {
                    val post = document.toObject(Post::class.java)
                    fetchedPosts.add(post)
                }
                _posts.value += fetchedPosts
            }
            .addOnFailureListener { exception ->
                Log.e("Firebase", "Error fetching posts", exception)
            }
    }

    fun getPostsFromCurrentUser(
        onSuccess: (List<Post>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val currentUserId = getCurrentUserId() ?: return
        db.collection("posts")
            .whereEqualTo("userId", currentUserId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val posts = mutableListOf<Post>()
                for (document in querySnapshot) {
                    val post = document.toObject(Post::class.java)
                    posts.add(post)
                }
                onSuccess(posts)
            }
            .addOnFailureListener { exception ->
                val errorMessage = "Error fetching current user's posts: ${exception.localizedMessage}"
                Log.e("Firebase", errorMessage)
                onFailure(errorMessage)
            }
    }

    fun getPostsFromUserId(userId: String, onSuccess: (List<Post>) -> Unit, onFailure: (String) -> Unit) {
        db.collection("posts")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val posts = mutableListOf<Post>()
                for (document in querySnapshot) {
                    val post = document.toObject(Post::class.java)
                    posts.add(post)
                }
                onSuccess(posts)
            }
            .addOnFailureListener { exception ->
                val errorMessage = "Error fetching current user's posts: ${exception.localizedMessage}"
                Log.e("Firebase", errorMessage)
                onFailure(errorMessage)
            }
    }

    fun searchPostByHashtag(hashtag: String, onSuccess: (List<Post>) -> Unit, onFailure: (String) -> Unit) {
        val searchTerm = hashtag.lowercase()

        db.collection("posts")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val filteredPosts = querySnapshot.toObjects(Post::class.java)
                    .filter { post ->
                        post.hashtag?.any { tag ->
                            tag.lowercase().contains(searchTerm)
                        } == true
                    }
                onSuccess(filteredPosts)
            }
            .addOnFailureListener { e ->
                onFailure("Failed to retrieve posts: ${e.message}")
            }
    }

    fun searchPostByTitle(title: String, onSuccess: (List<Post>) -> Unit, onFailure: (String) -> Unit) {
        // Convert search term to lowercase for case-insensitive search
        val searchTerm = title.lowercase()

        db.collection("posts")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val filteredPosts = querySnapshot.toObjects(Post::class.java)
                    .filter { post ->
                        post.title?.lowercase()?.contains(searchTerm) == true
                    }
                onSuccess(filteredPosts)
            }
            .addOnFailureListener { e ->
                onFailure("Failed to retrieve posts: ${e.message}")
            }
    }

    fun getPost(
        postId: String,
        onSuccess: (Post?) -> Unit,
        onFailure: (String) -> Unit
    ) {
        db.collection("posts")
            .document(postId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val post = document.toObject(Post::class.java)
                    onSuccess(post)
                } else {
                    onSuccess(null)
                }
            }
            .addOnFailureListener { exception ->
                val errorMessage = "Error fetching post with ID $postId: ${exception.localizedMessage}"
                Log.e("Firebase", errorMessage)
                onFailure(errorMessage)
            }
    }
}

