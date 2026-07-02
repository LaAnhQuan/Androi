package com.shopping.app.ui.chat.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
                    _messagesLiveData.value = value.toObjects(ChatMessage::class.java)
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
