package com.shopping.app.ui.order.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shopping.app.data.repository.order.OrderRepository

class OrderViewModelFactory(private val orderRepository: OrderRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return OrderViewModel(orderRepository) as T
    }
}
