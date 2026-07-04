package com.shopping.app.data.repository.category

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shopping.app.utils.Constants

class CategoryRepositoryImpl : CategoryRepository {

    override fun getCategories(): Task<QuerySnapshot> {
        return Firebase.firestore.collection(Constants.DATABASE_CATEGORIES_TABLE).get()
    }

}
