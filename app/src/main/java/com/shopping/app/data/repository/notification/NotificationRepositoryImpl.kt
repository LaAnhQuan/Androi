package com.shopping.app.data.repository.notification

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shopping.app.data.model.Notification
import com.shopping.app.utils.Constants

class NotificationRepositoryImpl : NotificationRepository {

    private fun notifications() =
        Firebase.firestore.collection(Constants.DATABASE_NOTIFICATIONS_TABLE)

    override fun addNotification(notification: Notification): Task<Void> {
        val doc = notifications().document()
        notification.id = doc.id
        return doc.set(notification)
    }

    override fun getNotifications(uid: String): Query {
        // sorted client-side by createdAt (avoids composite index)
        return notifications().whereEqualTo("userId", uid)
    }

}
