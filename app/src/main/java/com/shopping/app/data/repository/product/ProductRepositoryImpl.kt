package com.shopping.app.data.repository.product

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shopping.app.data.model.Product
import com.shopping.app.utils.Constants

class ProductRepositoryImpl : ProductRepository {

    override fun getProducts(): Task<QuerySnapshot> {
        return Firebase.firestore.collection(Constants.DATABASE_PRODUCTS_TABLE).get()
    }

    override fun addProduct(product: Product): Task<Void> {

        val collection = Firebase.firestore.collection(Constants.DATABASE_PRODUCTS_TABLE)
        val doc = collection.document() // auto-generated id

        product.id = doc.id
        product.sellerId = FirebaseAuth.getInstance().uid

        return doc.set(product)

    }

}
