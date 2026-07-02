package com.shopping.app.ui.notifications.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.shopping.app.data.model.Notification
import com.shopping.app.data.repository.notification.NotificationRepository

class NotificationsViewModel(private val notificationRepository: NotificationRepository) : ViewModel() {

    private val _notificationsLiveData = MutableLiveData<List<Notification>>()
    val notificationsLiveData: LiveData<List<Notification>>
        get() = _notificationsLiveData

    private var registration: ListenerRegistration? = null

    init {
        listen()
    }

    private fun listen() {

        val uid = FirebaseAuth.getInstance().uid ?: return

        registration = notificationRepository.getNotifications(uid)
            .addSnapshotListener { value, error ->
                if (error == null && value != null) {
                    val list = value.toObjects(Notification::class.java)
                        .sortedByDescending { it.createdAt?.toDate()?.time ?: 0L }
                    _notificationsLiveData.value = list
                }
            }

    }

    override fun onCleared() {
        super.onCleared()
        registration?.remove()
    }

}
