package com.example.savoreel.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.util.Date

data class Notification(
    val notificationId: String = "",
    val recipientId: String = "",
    val type: String = "",
    val senderId: String = "",
    val postId: String = "",
    val date: Date = Date(),
    val description: String = "",
    var isRead: Boolean = false
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "notificationId" to notificationId,
            "recipientId" to recipientId,
            "type" to type,
            "senderId" to senderId,
            "postId" to postId,
            "date" to date,
            "description" to description,
            "isRead" to isRead
        )
    }
}

sealed class NotificationState {
    data class Success(val notifications: List<Notification>) : NotificationState()
    data class Error(val message: String) : NotificationState()
    object Loading : NotificationState()
}

class NotificationViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _notifications = MutableLiveData<NotificationState>()
    val notifications: LiveData<NotificationState> get() = _notifications

    fun addNotification(
        notification: Notification,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val notificationId = db.collection("notifications").document().id
        val notificationWithId = notification.copy(notificationId = notificationId)

        db.collection("notifications").document(notificationId)
            .set(notificationWithId.toMap())
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e.localizedMessage ?: "Error adding notification.") }
    }

    fun getNotificationsForRecipient(
        recipientId: String,
        onSuccess: (List<Notification>) -> Unit,
        onFail: (String) -> Unit
    ) {
        db.collection("notifications")
            .whereEqualTo("recipientId", recipientId)
            .get()
            .addOnSuccessListener { snapshot ->
                val notifications = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Notification::class.java)
                }
                onSuccess(notifications)
            }
            .addOnFailureListener { e ->
                onFail(e.localizedMessage ?: "Error fetching notifications.")
            }
    }

    fun markNotificationAsRead(
        notificationId: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        db.collection("notifications").document(notificationId)
            .update("isRead", true)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e.localizedMessage ?: "Error updating notification.")}
    }

    fun deleteNotification(
        notificationId: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        db.collection("notifications").document(notificationId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e.localizedMessage ?: "Error deleting notification.") }
    }

    private fun mapSnapshotToNotifications(snapshot: QuerySnapshot): List<Notification> {
        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(Notification::class.java)
        }
    }
}