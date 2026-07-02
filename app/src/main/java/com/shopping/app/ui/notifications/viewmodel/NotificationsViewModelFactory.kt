package com.shopping.app.ui.notifications.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shopping.app.data.repository.notification.NotificationRepository

class NotificationsViewModelFactory(private val notificationRepository: NotificationRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NotificationsViewModel(notificationRepository) as T
    }
}
