package com.shopping.app.data.repository.chat

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shopping.app.data.model.Chat
import com.shopping.app.data.model.ChatMessage
import com.shopping.app.utils.Constants

class ChatRepositoryImpl : ChatRepository {

    private val db = Firebase.firestore
    private fun chats() = db.collection(Constants.DATABASE_CHATS_TABLE)

    override fun getUserChats(uid: String): Query {
        // Conversations where the current user is a participant.
        // (Sorted client-side by updatedAt to avoid a composite index.)
        return chats().whereArrayContains("participants", uid)
    }

    override fun getMessages(chatId: String): Query {
        return chats().document(chatId)
            .collection(Constants.DATABASE_MESSAGES_TABLE)
            .orderBy("sentAt", Query.Direction.ASCENDING)
    }

    override fun sendMessage(chat: Chat, message: ChatMessage): Task<DocumentReference> {

        val doc = chats().document(chat.chatId!!)

        // 1) create/update the conversation summary FIRST (so security rules can
        //    verify the sender is a participant before the message is written),
        // 2) then add the message to the sub-collection.
        return doc.set(chat, SetOptions.merge())
            .continueWithTask {
                doc.collection(Constants.DATABASE_MESSAGES_TABLE).add(message)
            }

    }

}
