package com.shopping.app.data.repository.product

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import com.shopping.app.data.model.Product

interface ProductRepository {

    fun getProducts(): Task<QuerySnapshot>

    fun addProduct(product: Product): Task<Void>

}
