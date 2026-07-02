package com.shopping.app.data.model

data class Chat(
    var chatId: String? = null,
    var participants: List<String>? = null,
    var names: Map<String, String>? = null,
    var lastMessage: String? = null,
    var updatedAt: Long? = 0L
)
