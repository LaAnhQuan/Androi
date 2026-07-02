package com.shopping.app.data.repository.order

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import com.shopping.app.data.model.Order

interface OrderRepository {

    fun addOrder(order: Order): Task<Void>

    fun getUserOrders(uid: String): Task<QuerySnapshot>

}
