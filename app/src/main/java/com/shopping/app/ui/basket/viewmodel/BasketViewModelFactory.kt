package com.shopping.app.ui.basket.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shopping.app.data.repository.basket.BasketRepository
import com.shopping.app.data.repository.order.OrderRepository

class BasketViewModelFactory(
    private val basketRepository: BasketRepository,
    private val orderRepository: OrderRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BasketViewModel(basketRepository, orderRepository) as T
    }
}
