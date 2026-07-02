package com.shopping.app.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class Notification(
    var id: String? = null,
    var userId: String? = null,   // recipient uid
    var title: String? = null,
    var body: String? = null,
    var type: String? = null,     // "message" | "order"
    // who triggered it (for "message": the sender, so tapping opens that chat)
    var fromUid: String? = null,
    var fromName: String? = null,
    var read: Boolean = false,
    @ServerTimestamp
    var createdAt: Timestamp? = null
)
