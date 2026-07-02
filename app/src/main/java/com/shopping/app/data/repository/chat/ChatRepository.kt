package com.shopping.app.data.repository.chat

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.shopping.app.data.model.Chat
import com.shopping.app.data.model.ChatMessage

interface ChatRepository {

    fun getUserChats(uid: String): Query

    fun getMessages(chatId: String): Query

    fun sendMessage(chat: Chat, message: ChatMessage): Task<DocumentReference>

}
