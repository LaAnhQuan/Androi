package com.shopping.app.data.model

data class ChatMessage(
    var senderId: String? = null,
    var text: String? = null,
    var sentAt: Long? = 0L,
    // optional: the product this message is about (Product serialized as JSON)
    var productJson: String? = null
)
