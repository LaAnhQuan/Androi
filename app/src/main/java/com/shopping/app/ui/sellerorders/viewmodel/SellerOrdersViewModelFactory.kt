package com.shopping.app.ui.sellerorders.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shopping.app.data.repository.order.OrderRepository

class SellerOrdersViewModelFactory(private val orderRepository: OrderRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SellerOrdersViewModel(orderRepository) as T
    }
}
