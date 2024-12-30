@file:Suppress("DEPRECATION")

package com.example.savoreel.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Suppress("DEPRECATION")
class UserViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> get() = _user

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private var signInSuccess: (() -> Unit)? = null

    private fun getCurrentUserId(): String? {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return null
        return currentUser.uid
    }

    fun setUser(user: User?) {
        _user.value = user
    }

    // Function to sign in a user
    fun signIn(email: String, password: String, onSuccess: (String) -> Unit, onFailure: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in successful
                    val firebaseUser = auth.currentUser
                    if (firebaseUser != null) {
                        onSuccess(firebaseUser.uid)
                    } else {
                        onFailure("Failed to retrieve user ID after sign-in.")
                    }
                } else {
                    // Sign in failed
                    onFailure(task.exception?.localizedMessage ?: "Unknown error occurred")
                }
            }
    }

    // Function to validate password
    fun validatePassword(password: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val userId = getCurrentUserId()?: return
        db.collection("users").document(userId).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val email = task.result?.getString("email")
                    // Check if the email exists and then proceed with password validation
                    if (!email.isNullOrEmpty()) {
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { authTask ->
                                if (authTask.isSuccessful) {
                                    onSuccess()
                                } else {
                                    onFailure("Incorrect password. Please try again.")
                                }
                            }
                    } else {
                        onFailure("User email not found.")
                    }
                } else {
                    onFailure("Failed to retrieve user information.")
                }
            }
    }

    // Function to create a new account
    fun createAccount(email: String, password: String, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        Log.d("CreateAccount", "Attempting to create account with email: $email")

        auth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { checkTask ->
                if (checkTask.isSuccessful) {
                    val signInMethods = checkTask.result?.signInMethods
                    if (!signInMethods.isNullOrEmpty()) {
                        Log.e("CreateAccount", "Email is already in use.")
                        onFailure("Email is already in use. Please log in or reset your password.")
                    } else {
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d("CreateAccount", "Account created successfully.")
                                    val firebaseUser = auth.currentUser
                                    if (firebaseUser != null) {
                                        val userId = firebaseUser.uid
                                        Log.d("CreateAccount", "User ID retrieved: $userId")
                                        val newUser = User(
                                            userId = userId,
                                            name = "",
                                            email = email,
                                            avatarUrl = "",
                                            darkModeEnabled = false,
                                            following = emptyList(),
                                            followers = emptyList(),
                                            nameKeywords = emptyList(),
                                            hashtags = emptyList(),
                                            searchHistory = emptyList()
                                        )
                                        db.collection("users").document(userId).set(newUser)
                                            .addOnSuccessListener {
                                                Log.d("CreateAccount", "User data saved to Firestore successfully.")
                                                onSuccess(userId)
                                            }
                                            .addOnFailureListener {
                                                Log.e("CreateAccount", "Failed to save user data: ${it.localizedMessage}", it)
                                                onFailure(it.localizedMessage ?: "Failed to save user data.")
                                            }
                                    } else {
                                        Log.e("CreateAccount", "Failed to retrieve user ID after account creation.")
                                        onFailure("Failed to retrieve user ID after account creation.")
                                    }
                                } else {
                                    val exception = task.exception
                                    if (exception is FirebaseAuthUserCollisionException) {
                                        Log.e("CreateAccount", "Email is already in use by another account.")
                                        onFailure("Email is already in use. Please log in or try another email.")
                                    } else {
                                        Log.e("CreateAccount", "Account creation failed: ${exception?.localizedMessage}", exception)
                                        onFailure(exception?.localizedMessage ?: "Account creation failed.")
                                    }
                                }
                            }
                    }
                } else {
                    Log.e("CreateAccount", "Failed to check email existence: ${checkTask.exception?.localizedMessage}", checkTask.exception)
                    onFailure(checkTask.exception?.localizedMessage ?: "Failed to check email existence.")
                }
            }
    }

    // Function to retrieve user details
    fun getUser(onSuccess: (User?) -> Unit, onFailure: (String) -> Unit) {
        val userId = getCurrentUserId() ?: return
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val userData = document.toObject(User::class.java)
                    setUser(userData)
                    onSuccess(userData)
                } else {
                    _user.value = null
                    onSuccess(null)
                }
            }
            .addOnFailureListener {
                onFailure("Failed to get user data.")
            }
    }

    private fun generateNameKeywords(name: String): List<String> {
        val words = name.toLowerCase().split(" ")
        val keywords = mutableSetOf<String>()
        for (word in words) {
            for (i in 1..word.length) {
                keywords.add(word.substring(0, i))
            }
        }
        return keywords.toList()
    }

    fun updateHashtags(hashtag: String) {
        val userId = getCurrentUserId()?: return
        val userDocRef = db.collection("users").document(userId)

        userDocRef.update("Hashtags", FieldValue.arrayUnion(hashtag))
            .addOnSuccessListener {
                println("Searched hashtags updated successfully.")
            }
            .addOnFailureListener { exception ->
                println("Failed to update searched hashtags: ${exception.localizedMessage}")
            }
    }

    fun updateSearchHistory(keyword: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val userId = getCurrentUserId() ?: return
        val timestamp = System.currentTimeMillis()
        val maxHistorySize = 10

        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                val searchHistory = user?.searchHistory?.toMutableList() ?: mutableListOf()

                val existingItem = searchHistory.find { it.keyword == keyword }
                if (existingItem != null) {
                    existingItem.timestamp = timestamp
                } else {
                    searchHistory.add(SearchHistoryItem(keyword, timestamp))
                }

                if (searchHistory.size > maxHistorySize) {
                    searchHistory.sortByDescending { it.timestamp }
                    searchHistory.removeAt(searchHistory.size - 1)
                }

                db.collection("users").document(userId)
                    .update("searchHistory", searchHistory)
                    .addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener {
                        onFailure(it.localizedMessage ?: "Failed to save search history.")
                    }
            }
            .addOnFailureListener {
                onFailure(it.localizedMessage ?: "Failed to retrieve user data.")
            }
    }

    fun getSearchHistory(onSuccess: (List<SearchHistoryItem>) -> Unit, onFailure: (String) -> Unit) {
        val userId = getCurrentUserId()?: return

        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                val searchHistory = user?.searchHistory ?: emptyList()
                val sortedSearchHistory = searchHistory.sortedByDescending { it.timestamp }

                onSuccess(sortedSearchHistory)
            }
            .addOnFailureListener {
                onFailure(it.localizedMessage ?: "Failed to retrieve search history.")
            }
    }

    // Function to update the user's name
    fun updateUserName(name: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val userId = getCurrentUserId()?: return
        val nameKeywords = generateNameKeywords(name)

        db.collection("users").document(userId)
            .update(mapOf(
                "name" to name,
                "nameKeywords" to nameKeywords
            ))
            .addOnSuccessListener {
                _user.value = _user.value?.copy(name = name) // Update user name immediately
                onSuccess()
            }
            .addOnFailureListener {
                onFailure(it.localizedMessage ?: "Error updating user name.")
            }
    }

    // Function to update the user's email
    fun updateUserEmail(newEmail: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            onFailure("User not logged in.")
            return
        }

        currentUser.updateEmail(newEmail)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = currentUser.uid
                    db.collection("users").document(userId)
                        .update("email", newEmail)
                        .addOnSuccessListener {
                            onSuccess()
                        }
                        .addOnFailureListener {
                            onFailure(it.localizedMessage ?: "Failed to update email in database.")
                        }
                } else {
                    onFailure(
                        task.exception?.localizedMessage ?: "Failed to update email in Firebase."
                    )
                }
            }
    }

    // Function to update the user's password
    fun updateUserPassword(newPass: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            onFailure("User not logged in.")
            return
        }

        currentUser.updatePassword(newPass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("UpdatePassword", "Password updated successfully in Firebase Authentication.")
                    onSuccess()
                } else {
                    val errorMessage = task.exception?.localizedMessage ?: "Failed to update password."
                    Log.e("UpdatePassword", errorMessage, task.exception)
                    onFailure(errorMessage)
                }
            }
    }

    // Function to update the user's avatar
    fun updateUserAvatar(avatarUrl: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val userId = getCurrentUserId()?: return
        db.collection("users").document(userId)
            .update("avatar", avatarUrl)
            .addOnSuccessListener {
                _user.value = _user.value?.copy(avatarUrl = avatarUrl)
                onSuccess()
            }
            .addOnFailureListener {
                onFailure(it.localizedMessage ?: "Error updating avatar.")
            }
    }

    // Function to send email to reset password
    fun sendPasswordResetEmail(email: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("UserViewModel", "Password reset email sent successfully.")
                    onSuccess()
                } else {
                    val errorMessage =
                        task.exception?.localizedMessage ?: "Failed to send reset email."
                    Log.e("UserViewModel", errorMessage, task.exception)
                    onFailure(errorMessage)
                }
            }
    }

    // Function to delete account
    fun deleteUserAndData(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            onFailure("User not logged in.")
            return
        }

        val userId = currentUser.uid

        // Bước 1: Truy vấn dữ liệu người dùng bị xóa
        val userRef = db.collection("users").document(userId)
        userRef.get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.exists()) {
                    onFailure("User data does not exist.")
                    return@addOnSuccessListener
                }

                // Lấy danh sách người dùng đang follow mình
                val followersList = (snapshot.get("followers") as? List<*>)?.filterIsInstance<String>() ?: emptyList()
                // Lấy danh sách người dùng mình đang follow
                val followingList = (snapshot.get("following") as? List<*>)?.filterIsInstance<String>() ?: emptyList()

                val batch = db.batch()

                // Bước 2: Xóa userId khỏi danh sách `followers` của những người mà mình follow
                followingList.forEach { followedUserId ->
                    val followedUserRef = db.collection("users").document(followedUserId)
                    batch.update(followedUserRef, "followers", FieldValue.arrayRemove(userId))
                }

                // Bước 3: Xóa userId khỏi danh sách `following` của những người đang follow mình
                followersList.forEach { followerUserId ->
                    val followerUserRef = db.collection("users").document(followerUserId)
                    batch.update(followerUserRef, "following", FieldValue.arrayRemove(userId))
                }

                // Xóa chính người dùng này khỏi Firestore
                batch.delete(userRef)

                // Thực thi batch
                batch.commit()
                    .addOnSuccessListener {
                        Log.d("FirebaseAuth", "User data deleted from Firestore and relationships updated.")

                        // Bước 4: Xóa tài khoản người dùng từ Firebase Authentication
                        currentUser.delete()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d(
                                        "FirebaseAuth",
                                        "User account deleted from Firebase Authentication."
                                    )
                                    onSuccess()
                                } else {
                                    Log.e(
                                        "FirebaseAuth",
                                        "Failed to delete user account: ${task.exception?.message}"
                                    )
                                    onFailure("Failed to delete user account.")
                                }
                            }
                    }
                    .addOnFailureListener { e ->
                        Log.e("FirebaseAuth", "Failed to delete user data: ${e.message}")
                        onFailure("Failed to delete user data.")
                    }
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseAuth", "Failed to retrieve user data: ${e.message}")
                onFailure("Failed to retrieve user data.")
            }
    }

    // Sign in with Google
    fun signInWithGoogle(account: GoogleSignInAccount) {
        val idToken = account.idToken ?: return
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                if (user != null) {
                    val userId = user.uid
                    val userDocRef = db.collection("users").document(userId)
                    userDocRef.get().addOnSuccessListener { document ->
                        if (!document.exists()) {
                            val name = user.displayName
                            val email = user.email
                            val avatarUrl = user.photoUrl?.toString()
                            val nameKeywords = generateNameKeywords(name.toString())

                            val newUser = User(
                                userId = userId,
                                name = name,
                                email = email,
                                avatarUrl = avatarUrl,
                                darkModeEnabled = false,
                                following = emptyList(),
                                followers = emptyList(),
                                nameKeywords = nameKeywords,
                                hashtags = emptyList(),
                                searchHistory = emptyList()
                            )

                            userDocRef.set(newUser)
                                .addOnSuccessListener {
                                    Log.d("CreateAccount", "User data saved to Firestore successfully.")
                                    signInSuccess?.invoke()
                                }
                                .addOnFailureListener { e ->
                                    Log.e("CreateAccount", "Failed to save user data: ${e.message}")
                                }
                        } else {
                            Log.d("CreateAccount", "User data already exists in Firestore.")
                            signInSuccess?.invoke()
                        }
                    }
                }
            } else {
                Log.w("SignInActivity", "Google sign-in failed", task.exception)
            }
        }
    }

    fun setSignInSuccessListener(onSuccess: () -> Unit) {
        signInSuccess = onSuccess
    }

    private fun createFollowNotification(
        recipientId: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val currentUser = auth.currentUser ?: run {
            onFailure("User not logged in")
            return
        }

        val notification = Notification(
            notificationId = db.collection("notifications").document().id,
            recipientId = recipientId,
            senderId = currentUser.uid,
            description = "started following you",
            type = "follow"
        )

        db.collection("notifications").document(notification.notificationId)
            .set(notification)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it.message ?: "Failed to create notification") }
    }

    // Update toggleFollowStatus to include notification
    fun toggleFollowStatus(
        userId: String,
        onSuccess: (Boolean) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val currentUserId = getCurrentUserId()?: return

        val currentUserRef = db.collection("users").document(currentUserId)
        val targetUserRef = db.collection("users").document(userId)

        db.runTransaction { transaction ->
            val currentUserSnapshot = transaction.get(currentUserRef)
            val currentFollowingList = (currentUserSnapshot.get("following") as? List<*>)
                ?.filterIsInstance<String>()
                ?: emptyList()

            val shouldFollow = !currentFollowingList.contains(userId)

            if (shouldFollow) {
                transaction.update(currentUserRef, "following", FieldValue.arrayUnion(userId))
                transaction.update(targetUserRef, "followers", FieldValue.arrayUnion(currentUserId))
            } else {
                transaction.update(currentUserRef, "following", FieldValue.arrayRemove(userId))
                transaction.update(targetUserRef, "followers", FieldValue.arrayRemove(currentUserId))
            }
            shouldFollow
        }.addOnSuccessListener { isFollowing ->
            if (isFollowing) {
                createFollowNotification(userId, {}, { error -> Log.e("Notification", error) })
            }

            val updatedFollowingList = if (isFollowing) {
                _user.value?.following?.plus(userId) ?: listOf(userId)
            } else {
                _user.value?.following?.minus(userId) ?: emptyList()
            }
            _user.value = _user.value?.copy(following = updatedFollowingList)

            onSuccess(isFollowing)
        }.addOnFailureListener { exception ->
            onFailure("Failed to toggle follow status: ${exception.localizedMessage}")
        }
    }

    fun isFollowing(userId: String, onSuccess: (Boolean) -> Unit, onFailure: (String) -> Unit) {
        val currentUserId = getCurrentUserId() ?: return
        db.collection("users").document(currentUserId).get()
            .addOnSuccessListener { document ->
                val userData = document.toObject(User::class.java)
                val isFollowing = userData?.following?.contains(userId) ?: false
                onSuccess(isFollowing)
            }
            .addOnFailureListener { exception ->
                onFailure("Failed to check following status: ${exception.localizedMessage}")
            }
    }

    fun getUserById(
        userId: String,
        onSuccess: (User?) -> Unit,
        onFailure: (String) -> Unit
    ){
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val userData = document.toObject(User::class.java)
                    onSuccess(userData)
                } else {
                    onSuccess(null)
                }
            }
            .addOnFailureListener {
                onFailure("Failed to get user data.")
            }
    }

    fun getFollowers(
        userId : String,
        onSuccess: (List<String>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        Log.d("getFollowers", "Fetching followers for userId: $userId")

        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(User::class.java)
                    val followersIds = user?.followers ?: emptyList()

                    Log.d("getFollowers", "Followers IDs: $followersIds")
                    onSuccess(followersIds)
                } else {
                    Log.d("getFollowers", "No followers found for userId: $userId")
                    onSuccess(emptyList())
                }
            }
            .addOnFailureListener {
                Log.e("getFollowers", "Failed to fetch followers for userId: $userId")
                onFailure("Failed to fetch followers for userId: $userId")
            }
    }

    fun getFollowing(
        userId : String,
        onSuccess: (List<String>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        Log.d("getFollowing", "Fetching following for userId: $userId")

        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(User::class.java)
                    val followingIds = user?.following ?: emptyList()

                    Log.d("getFollowing", "Following IDs: $followingIds")
                    onSuccess(followingIds)
                } else {
                    Log.d("getFollowing", "No following found for userId: $userId")
                    onSuccess(emptyList())
                }
            }
            .addOnFailureListener {
                Log.e("getFollowing", "Failed to fetch following for userId: $userId")
                onFailure("Failed to fetch following for userId: $userId")
            }
    }

    fun getAllUsersByNameKeyword(
        keyword: String,
        onSuccess: (List<User>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId == null) {
            onFailure("User not logged in.")
            return
        }

        val lowerCaseKeyword = keyword.toLowerCase()

        db.collection("users")
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    onFailure("No users found in the database.")
                } else {
                    val users = documents.mapNotNull { doc ->
                        val user = doc.toObject(User::class.java)
                        if (doc.id != currentUserId) user else null
                    }.filter { user ->
                        val nameKeywords = user.nameKeywords ?: emptyList()
                        nameKeywords.any { it == lowerCaseKeyword }
                    }

                    if (users.isEmpty()) {
                        onFailure("No users found matching the keyword '$keyword'.")
                    } else {
                        onSuccess(users)
                    }
                }
            }
            .addOnFailureListener { exception ->
                onFailure("Failed to get users: ${exception.localizedMessage}")
            }
    }
}