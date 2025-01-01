package com.example.savoreel.model

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.savoreel.sendSystemNotification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NotificationViewModel: ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount

    private var notificationListener: ListenerRegistration? = null

    private fun getCurrentUserId(): String? {
        val currentUser = auth.currentUser ?: return null
        return currentUser.uid
    }

    private fun log(message: String) {
        Log.d("NotificationViewModel", message)
    }

    init {
        observeUnreadNotifications()
        observeNotifications()
    }

    private fun observeNotifications() {
        val currentUser = auth.currentUser ?: run {
            log("No authenticated user.")
            return
        }

        log("Start observing notifications")

        // Truy vấn thông tin người dùng từ database
        getUserById(
            currentUser.uid,
            onSuccess = { user ->
                val name = user?.name ?: "User"

                notificationListener = db.collection("notifications")
                    .whereEqualTo("recipientId", currentUser.uid)
                    .orderBy("date", Query.Direction.DESCENDING)
                    .addSnapshotListener { snapshot, e ->
                        if (e != null) {
                            log("Error fetching notifications: ${e.message}")
                            return@addSnapshotListener
                        }

                        snapshot?.let {
                            val newNotificationList = it.documents.mapNotNull { doc ->
                                doc.toObject(Notification::class.java)
                            }

                            val oldNotifications = _notifications.value
                            _notifications.value = newNotificationList

                            // Lọc các thông báo mới
                            val newNotifications = newNotificationList.filterNot { notification ->
                                oldNotifications?.any { it.notificationId == notification.notificationId } == true
                            }

                            // Gửi thông báo mới nhất
                            if (newNotifications.isNotEmpty()) {
                                val latestNotification = newNotifications.first()
                                sendSystemNotification(name, latestNotification)
                            }
                        }
                    }
            },
            onFailure = { error ->
                log("Failed to fetch user: $error")
            }
        )
    }

    private fun observeUnreadNotifications() {
        val currentUser = auth.currentUser ?: run {
            return
        }
        db.collection("notifications")
            .whereEqualTo("recipientId", currentUser.uid)
            .whereEqualTo("read", false)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.e("NotificationViewModel", "Error observing notifications", error)
                    return@addSnapshotListener
                }

                snapshots?.let {
                    _unreadCount.value = it.size()
                }
            }
    }

    fun startObservingNotifications(onError: (String) -> Unit) {
        val currentUser = auth.currentUser ?: run {
            return
        }
        log("Start observing notifications")
        notificationListener = db.collection("notifications")
            .whereEqualTo("recipientId", currentUser.uid)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    onError(e.message ?: "Error fetching notifications")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val notificationList = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Notification::class.java)
                    }
                    _notifications.value = notificationList
                    _unreadCount.value = notificationList.count { !it.read }
                    log("Notifications updated: ${notificationList.size} items")
                }
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

    fun stopObservingNotifications() {
        log("Stop observing notifications")
        notificationListener?.remove()
        notificationListener = null
    }

    fun createNotifications(
        recipientIds: List<String>,
        postId: String,
        type: String,
        message: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        log("Creating notifications for recipients: ${recipientIds.size}")
        val currentUser = auth.currentUser ?: run {
            onFailure("User not logged in")
            log("User not logged in")
            return
        }

        if (recipientIds.isEmpty()) {
            onFailure("User not logged in")
            log("User not logged in")
            return
        }

        val batch = db.batch()
        recipientIds.forEach { recipientId ->
            val notificationId = db.collection("notifications").document().id
            val notification = Notification(
                notificationId = notificationId,
                recipientId = recipientId,
                postId = postId,
                senderId = currentUser.uid,
                description = message,
                type = type
            )

            val notificationRef = db.collection("notifications").document(notificationId)
            batch.set(notificationRef, notification)
        }

        batch.commit()
            .addOnSuccessListener {
                onSuccess()
                log("Notifications created successfully")
            }
            .addOnFailureListener { exception ->
                log("Error creating notifications: ${exception.message}")
                onFailure(exception.message ?: "Failed to create notifications")
            }
    }

    fun createNotification(
        recipientId: String,
        postId: String,
        type: String,
        message: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        log("Creating notification for recipient: $recipientId")
        val currentUser = auth.currentUser ?: run {
            onFailure("User not logged in")
            log("User not logged in")
            return
        }

        if (recipientId.isEmpty()) {
            onFailure("Recipient ID is empty")
            log("Recipient ID is empty")
            return
        }

        val notificationId = db.collection("notifications").document().id
        val notification = Notification(
            notificationId = notificationId,
            recipientId = recipientId,
            postId = postId,
            senderId = currentUser.uid,
            description = message,
            type = type
        )

        val notificationRef = db.collection("notifications").document(notificationId)
        notificationRef.set(notification)
            .addOnSuccessListener {
                onSuccess()
                log("Notification created successfully for recipient: $recipientId")
            }
            .addOnFailureListener { exception ->
                log("Error creating notification: ${exception.message}")
                onFailure(exception.message ?: "Failed to create notification")
            }
    }

    fun getNotifications(onSuccess: (List<Notification>) -> Unit, onFailure: (String) -> Unit) {
        val currentUser = auth.currentUser ?: run {
            onFailure("Not logged in")
            return
        }

        db.collection("notifications")
            .whereEqualTo("recipientId", currentUser.uid)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    onFailure(e.message ?: "Error fetching notifications")
                    return@addSnapshotListener
                }

                val notificationList = snapshot?.toObjects(Notification::class.java) ?: emptyList()
                _notifications.value = notificationList // Update state
                onSuccess(notificationList)
            }
    }

    fun markAsRead(notificationId: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        log("Marking notification $notificationId as read")
        db.collection("notifications")
            .document(notificationId)
            .update("read", true)
            .addOnSuccessListener {
                onSuccess()
                updateNotifications()
                log("Notification $notificationId marked as read")
            }
            .addOnFailureListener { exception ->
                onFailure(exception.message ?: "Error marking notification as read")
                log("Error marking notification $notificationId as read: ${exception.message}")
            }
    }

    fun markAllAsRead(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val currentUser = auth.currentUser ?: run {
            onFailure("Not logged in")
            return
        }

        db.collection("notifications")
            .whereEqualTo("recipientId", currentUser.uid)
            .whereEqualTo("read", false)
            .get()
            .addOnSuccessListener { documents ->
                val batch = db.batch()
                documents.forEach { doc ->
                    batch.update(doc.reference, "read", true)
                }
                batch.commit()
                    .addOnSuccessListener {
                        onSuccess()
                        updateNotifications()
                    }
                    .addOnFailureListener { onFailure(it.message ?: "Error marking all as read") }
            }
            .addOnFailureListener { onFailure(it.message ?: "Error fetching notifications") }
    }

    fun countUnreadNotifications() {
        val currentUser = auth.currentUser ?: run {
            return
        }

        db.collection("notifications")
            .whereEqualTo("recipientId", currentUser.uid)
            .whereEqualTo("read", false)
            .get()
            .addOnSuccessListener { documents ->
                val count = documents.size()
                _unreadCount.value = count
            }
            .addOnFailureListener {
            }
    }

    fun deleteNotification(notificationId: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        db.collection("notifications")
            .document(notificationId)
            .delete()
            .addOnSuccessListener {
                onSuccess()
                updateNotifications()
            }
            .addOnFailureListener { onFailure(it.message ?: "Error deleting notification") }
    }

    fun deleteAllNotifications(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val currentUser = auth.currentUser ?: run {
            onFailure("Not logged in")
            return
        }

        db.collection("notifications")
            .whereEqualTo("recipientId", currentUser.uid)
            .get()
            .addOnSuccessListener { documents ->
                val batch = db.batch()
                documents.forEach { doc ->
                    batch.delete(doc.reference)
                }
                batch.commit()
                    .addOnSuccessListener {
                        onSuccess()
                        updateNotifications()
                    }
                    .addOnFailureListener { onFailure(it.message ?: "Error deleting notifications") }
            }
            .addOnFailureListener { onFailure(it.message ?: "Error fetching notifications") }
    }

    private fun updateNotifications() {
        log("Updating notifications")
        val currentUser = auth.currentUser ?: return
        db.collection("notifications")
            .whereEqualTo("recipientId", currentUser.uid)
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                val notificationList = snapshot.toObjects(Notification::class.java)
                _notifications.value = notificationList
                _unreadCount.value = notificationList.count { !it.read }
                log("Notifications updated: ${notificationList.size} items")
            }
            .addOnFailureListener { exception ->
                log("Error updating notifications: ${exception.message}")
            }
    }
}