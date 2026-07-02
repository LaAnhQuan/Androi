package com.shopping.app.ui.chat.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.shopping.app.data.model.Chat
import com.shopping.app.data.model.ChatMessage
import com.shopping.app.data.repository.chat.ChatRepository

class ChatViewModel(private val chatRepository: ChatRepository) : ViewModel() {

    private val _messagesLiveData = MutableLiveData<List<ChatMessage>>()
    val messagesLiveData: LiveData<List<ChatMessage>>
        get() = _messagesLiveData

    private var registration: ListenerRegistration? = null

    fun listenMessages(chatId: String) {

        registration?.remove()
        registration = chatRepository.getMessages(chatId)
            .addSnapshotListener { value, error ->
                if (error == null && value != null) {
                    val messages = value.documents.mapNotNull { doc ->
                        // ESTIMATE gives a just-sent message a local time until the
                        // server confirms, so it shows a time immediately and in order.
                        runCatching {
                            doc.toObject(
                                ChatMessage::class.java,
                                DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                            )
                        }.getOrNull()
                    }
                    _messagesLiveData.value = messages
                }
            }

    }

    fun send(chat: Chat, message: ChatMessage) {
        chatRepository.sendMessage(chat, message)
    }

    override fun onCleared() {
        super.onCleared()
        registration?.remove()
    }

}
