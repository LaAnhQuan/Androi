package com.shopping.app.ui.chat.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ListenerRegistration
import com.shopping.app.data.model.Chat
import com.shopping.app.data.repository.chat.ChatRepository

class ChatListViewModel(private val chatRepository: ChatRepository) : ViewModel() {

    private val _chatsLiveData = MutableLiveData<List<Chat>>()
    val chatsLiveData: LiveData<List<Chat>>
        get() = _chatsLiveData

    private var registration: ListenerRegistration? = null

    fun listenChats(uid: String) {

        registration?.remove()
        registration = chatRepository.getUserChats(uid)
            .addSnapshotListener { value, error ->
                if (error == null && value != null) {
                    // sort newest first (client-side, avoids composite index)
                    val chats = value.toObjects(Chat::class.java)
                        .sortedByDescending { it.updatedAt ?: 0L }
                    _chatsLiveData.value = chats
                }
            }

    }

    override fun onCleared() {
        super.onCleared()
        registration?.remove()
    }

}
