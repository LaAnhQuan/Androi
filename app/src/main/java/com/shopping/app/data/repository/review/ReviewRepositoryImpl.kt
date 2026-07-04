package com.shopping.app.data.repository.review

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shopping.app.data.model.Review
import com.shopping.app.utils.Constants

class ReviewRepositoryImpl : ReviewRepository {

    private fun reviews() = Firebase.firestore.collection(Constants.DATABASE_REVIEWS_TABLE)

    override fun addReview(review: Review): Task<Void> {
        val doc = reviews().document()
        review.id = doc.id
        return doc.set(review)
    }

    override fun getReviews(productId: String): Query {
        // sorted client-side by createdAt (avoids composite index)
        return reviews().whereEqualTo("productId", productId)
    }

}
