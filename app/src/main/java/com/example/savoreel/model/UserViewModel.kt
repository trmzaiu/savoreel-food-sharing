package com.example.savoreel.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date

@Suppress("DEPRECATION")
class UserViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    // Function to sign in a user
    fun signIn(
        email: String,
        password: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
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

    // Funtion to validate password
    fun validatePassword(password: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            onFailure("User not logged in.")
            return
        }

        val userId = currentUser.uid
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
    fun createAccount(
        email: String,
        password: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
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
                                            name = null,
                                            email = email,
                                            avatarUrl = null,
                                            darkModeEnabled = false,
                                            following = emptyList(),
                                            followers = emptyList()
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
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            onFailure("User not logged in.")
            return
        }

        val userId = currentUser.uid
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

    // Function to update the user's name
    fun updateUserName(name: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            onFailure("User not logged in.")
            return
        }

        val userId = currentUser.uid
        db.collection("users").document(userId)
            .update("name", name)
            .addOnSuccessListener {
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
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            onFailure("User not logged in.")
            return
        }

        val userId = currentUser.uid
        db.collection("users").document(userId)
            .update("avatar", avatarUrl)
            .addOnSuccessListener {
                _user.postValue(_user.value?.copy(avatarUrl = avatarUrl))
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
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userId)
            .delete()
            .addOnSuccessListener {
                Log.d("FirebaseAuth", "User data deleted from Firestore.")

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
    fun createFollowNotification(
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
            type = "follow",
            senderId = currentUser.uid,
            date = Date(),
            description = "started following you"
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
        val currentUser = auth.currentUser ?: run {
            onFailure("User not logged in")
            return
        }

        val currentUserId = currentUser.uid
        val currentUserRef = db.collection("users").document(currentUserId)
        val targetUserRef = db.collection("users").document(userId)
        var currentFollowingList: List<String> = emptyList()

        db.runTransaction { transaction ->
            val currentUserSnapshot = transaction.get(currentUserRef)
            val targetUserSnapshot = transaction.get(targetUserRef)

            currentFollowingList = currentUserSnapshot.get("following") as? List<String> ?: emptyList()
            val isNowFollowing = !currentFollowingList.contains(userId)

            if (isNowFollowing) {
                transaction.update(currentUserRef, "following", FieldValue.arrayUnion(userId))
                transaction.update(targetUserRef, "followers", FieldValue.arrayUnion(currentUserId))
            } else {
                transaction.update(currentUserRef, "following", FieldValue.arrayRemove(userId))
                transaction.update(targetUserRef, "followers", FieldValue.arrayRemove(currentUserId))
            }
            isNowFollowing
        }.addOnSuccessListener { isFollowing ->
            if (isFollowing) {
                createFollowNotification(userId, {}, { error -> Log.e("Notification", error) })
            }
            val updatedFollowingList = if (isFollowing) currentFollowingList + userId else currentFollowingList - userId
            _user.postValue(_user.value?.copy(following = updatedFollowingList))
            onSuccess(isFollowing)
        }.addOnFailureListener { exception ->
            onFailure("Failed to toggle follow status: ${exception.localizedMessage}")
        }
    }


    fun isFollowing(userId: String, onSuccess: (Boolean) -> Unit, onFailure: (String) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            onFailure("User not logged in.")
            return
        }

        val currentUserId = currentUser.uid
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

    suspend fun getUserByID(userId: String): User? {
        return try {
            val document = db.collection("users").document(userId).get().await() // Use 'await' to make it suspending
            if (document.exists()) {
                document.toObject(User::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun getUsersByIds(userIds: List<String>, onSuccess: (List<User>) -> Unit, onFailure: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val usersDeferred = userIds.map { userId ->
                    async {
                        getUserByID(userId)
                    }
                }

                val users = usersDeferred.awaitAll().filterNotNull()

                onSuccess(users)
            } catch (e: Exception) {
                onFailure("Error loading users: ${e.message}")
            }
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
            .orderBy("name")
            .startAt(lowerCaseKeyword)
            .endAt(lowerCaseKeyword + "\uf8ff")
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    onFailure("No users found for the given keyword.")
                } else {
                    val users = documents.mapNotNull { doc ->
                        val user = doc.toObject(User::class.java)
                        if (doc.id != currentUserId) user else null // Exclude current user
                    }
                    if (users.isEmpty()) {
                        onFailure("No users found for the given keyword, excluding current user.")
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