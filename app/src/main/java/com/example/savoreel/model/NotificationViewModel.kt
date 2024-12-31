package com.example.savoreel.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class NotificationViewModel: ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _notifications = MutableLiveData<List<Notification>>()
    val notifications: LiveData<List<Notification>> = _notifications

    fun createNotifications(
        recipientIds: List<String>,
        type: String,
        message: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val currentUser = auth.currentUser ?: run {
            onFailure("User not logged in")
            return
        }

        if (recipientIds.isEmpty()) {
            onFailure("Recipient list is empty")
            return
        }

        val db = FirebaseFirestore.getInstance()
        val batch = db.batch()

        recipientIds.forEach { recipientId ->
            val notificationId = db.collection("notifications").document().id
            val notification = Notification(
                notificationId = notificationId,
                recipientId = recipientId,
                senderId = currentUser.uid,
                description = message,
                type = type
            )

            val notificationRef = db.collection("notifications").document(notificationId)
            batch.set(notificationRef, notification)
        }

        batch.commit()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception ->
                onFailure(exception.message ?: "Failed to create notifications")
            }
    }

    fun createNotification(
        recipientId: String,
        type: String,
        message: String,
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
            description = message,
            type = type
        )

        db.collection("notifications").document(notification.notificationId)
            .set(notification)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it.message ?: "Failed to create notification") }
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
                _notifications.value = notificationList
                onSuccess(notificationList)
            }
    }

    fun markAsRead(notificationId: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        db.collection("notifications")
            .document(notificationId)
            .update("read", true)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it.message ?: "Error marking notification as read") }
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
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onFailure(it.message ?: "Error marking all as read") }
            }
            .addOnFailureListener { onFailure(it.message ?: "Error fetching notifications") }
    }

    fun countUnreadNotifications(onSuccess: (Int) -> Unit, onFailure: (String) -> Unit) {
        val currentUser = auth.currentUser ?: run {
            onFailure("Not logged in")
            return
        }

        db.collection("notifications")
            .whereEqualTo("recipientId", currentUser.uid)
            .whereEqualTo("read", false)
            .get()
            .addOnSuccessListener { documents ->
                onSuccess(documents.size())
            }
            .addOnFailureListener { exception ->
                onFailure(exception.message ?: "Error counting unread notifications")
            }
    }


    fun deleteNotification(notificationId: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        db.collection("notifications")
            .document(notificationId)
            .delete()
            .addOnSuccessListener { onSuccess() }
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
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onFailure(it.message ?: "Error deleting notifications") }
            }
            .addOnFailureListener { onFailure(it.message ?: "Error fetching notifications") }
    }
}