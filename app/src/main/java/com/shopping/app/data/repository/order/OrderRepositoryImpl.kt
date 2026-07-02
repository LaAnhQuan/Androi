package com.shopping.app.data.repository.order

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shopping.app.data.model.Order
import com.shopping.app.utils.Constants

class OrderRepositoryImpl : OrderRepository {

    private fun orders() = Firebase.firestore.collection(Constants.DATABASE_ORDERS_TABLE)

    override fun addOrder(order: Order): Task<Void> {
        val doc = orders().document()
        order.orderId = doc.id
        return doc.set(order)
    }

    override fun getUserOrders(uid: String): Task<QuerySnapshot> {
        // sorted client-side by createdAt (avoids composite index)
        return orders().whereEqualTo("buyerId", uid).get()
    }

}
