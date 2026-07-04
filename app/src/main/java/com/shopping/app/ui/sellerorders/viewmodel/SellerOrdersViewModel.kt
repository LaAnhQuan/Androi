package com.shopping.app.ui.sellerorders.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.shopping.app.data.model.DataState
import com.shopping.app.data.model.Order
import com.shopping.app.data.repository.order.OrderRepository

class SellerOrdersViewModel(private val orderRepository: OrderRepository) : ViewModel() {

    private val _ordersLiveData = MutableLiveData<DataState<List<Order>?>>()
    val ordersLiveData: LiveData<DataState<List<Order>?>>
        get() = _ordersLiveData

    init {
        getOrders()
    }

    fun updateStatus(orderId: String, status: String) {
        orderRepository.updateOrderStatus(orderId, status)
            .addOnSuccessListener { getOrders() }
    }

    private fun getOrders() {

        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            _ordersLiveData.value = DataState.Error("Not signed in")
            return
        }

        _ordersLiveData.postValue(DataState.Loading())
        orderRepository.getSellerOrders(uid)
            .addOnSuccessListener { snapshot ->
                val orders = snapshot.toObjects(Order::class.java)
                    .sortedByDescending { it.createdAt ?: 0L }
                _ordersLiveData.postValue(DataState.Success(orders))
            }
            .addOnFailureListener { e ->
                _ordersLiveData.postValue(DataState.Error(e.message.toString()))
            }

    }

}
