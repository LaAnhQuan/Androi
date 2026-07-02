package com.shopping.app.data.repository.search

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot

interface SearchRepository {

    fun getProducts(): Task<QuerySnapshot>

    fun getProductsByCategory(category: String): Task<QuerySnapshot>

}
