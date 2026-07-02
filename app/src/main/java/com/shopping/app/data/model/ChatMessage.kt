package com.shopping.app.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class ChatMessage(
    var senderId: String? = null,
    var text: String? = null,
    // Filled by the Firestore SERVER on write (consistent across all devices,
    // so message order is correct even if users' device clocks differ).
    @ServerTimestamp
    var sentAt: Timestamp? = null,
    // optional: the product this message is about (Product serialized as JSON)
    var productJson: String? = null
)
