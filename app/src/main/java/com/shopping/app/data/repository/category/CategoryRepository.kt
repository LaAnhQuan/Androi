package com.shopping.app.data.repository.category

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot

interface CategoryRepository {

    fun getCategories(): Task<QuerySnapshot>

}
