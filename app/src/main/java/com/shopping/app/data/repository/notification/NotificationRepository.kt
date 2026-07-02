package com.shopping.app.data.repository.notification

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.Query
import com.shopping.app.data.model.Notification

interface NotificationRepository {

    fun addNotification(notification: Notification): Task<Void>

    fun getNotifications(uid: String): Query

}
