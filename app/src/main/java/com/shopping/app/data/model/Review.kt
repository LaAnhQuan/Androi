package com.shopping.app.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class Review(
    var id: String? = null,
    var productId: String? = null,
    var userId: String? = null,
    var userName: String? = null,
    var rating: Float = 0f,
    var comment: String? = null,
    @ServerTimestamp
    var createdAt: Timestamp? = null
)
