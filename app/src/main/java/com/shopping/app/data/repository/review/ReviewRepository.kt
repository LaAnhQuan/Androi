package com.shopping.app.data.repository.review

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.Query
import com.shopping.app.data.model.Review

interface ReviewRepository {

    fun addReview(review: Review): Task<Void>

    fun getReviews(productId: String): Query

}
