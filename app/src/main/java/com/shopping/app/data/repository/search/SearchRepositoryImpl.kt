package com.shopping.app.data.repository.search

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shopping.app.utils.Constants

class SearchRepositoryImpl : SearchRepository {

    override fun getProducts(): Task<QuerySnapshot> {
        return Firebase.firestore.collection(Constants.DATABASE_PRODUCTS_TABLE).get()
    }

    override fun getProductsByCategory(category: String): Task<QuerySnapshot> {
        return Firebase.firestore.collection(Constants.DATABASE_PRODUCTS_TABLE)
            .whereEqualTo("category", category)
            .get()
    }

}
