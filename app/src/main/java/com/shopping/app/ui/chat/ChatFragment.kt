package com.shopping.app.ui.chat

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.shopping.app.R
import com.shopping.app.data.model.Chat
import com.shopping.app.data.model.ChatMessage
import com.shopping.app.data.model.UserStatus
import com.shopping.app.data.preference.UserPref
import com.shopping.app.data.presence.PresenceManager
import com.shopping.app.data.repository.chat.ChatRepositoryImpl
import com.shopping.app.databinding.FragmentChatBinding
import com.shopping.app.ui.chat.adapter.MessageAdapter
import com.shopping.app.ui.chat.viewmodel.ChatViewModel
import com.shopping.app.ui.chat.viewmodel.ChatViewModelFactory
import com.shopping.app.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatFragment : Fragment() {

    private lateinit var bnd: FragmentChatBinding
    private val viewModel by viewModels<ChatViewModel> {
        ChatViewModelFactory(ChatRepositoryImpl())
    }

    private lateinit var adapter: MessageAdapter

    private val myUid: String by lazy { FirebaseAuth.getInstance().uid ?: "" }
    private var myName: String = ""
    private var otherUid: String = ""
    private var otherName: String = ""
    private lateinit var chatId: String
    private var pendingProductJson: String? = null

    // presence (other user's online status)
    private var statusRegistration: ListenerRegistration? = null
    private var lastStatus: UserStatus? = null
    private val statusHandler = Handler(Looper.getMainLooper())
    private val statusRefresh = object : Runnable {
        override fun run() {
            updateStatusUi()
            statusHandler.postDelayed(this, 30_000)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bnd = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false)
        return bnd.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        otherUid = arguments?.getString(Constants.CHAT_OTHER_UID) ?: ""
        otherName = arguments?.getString(Constants.CHAT_OTHER_NAME) ?: ""
        pendingProductJson = arguments?.getString(Constants.CHAT_PRODUCT_JSON)

        // deterministic chat id shared by both participants
        chatId = listOf(myUid, otherUid).sorted().joinToString("_")

        bnd.tvChatTitle.text = otherName

        adapter = MessageAdapter(emptyList(), myUid) { productJson -> openProduct(productJson) }
        bnd.rvMessages.layoutManager = LinearLayoutManager(requireContext()).apply { stackFromEnd = true }
        bnd.rvMessages.adapter = adapter

        viewModel.messagesLiveData.observe(viewLifecycleOwner) { messages ->
            adapter.update(messages)
            if (messages.isNotEmpty()) bnd.rvMessages.scrollToPosition(messages.size - 1)
        }

        viewModel.listenMessages(chatId)

        // load my display name, then enable sending
        val userPref = UserPref(requireContext())
        CoroutineScope(Dispatchers.Main).launch {
            myName = userPref.getUsername()
        }

        bnd.btnSend.setOnClickListener { sendMessage() }
        bnd.ivChatBack.setOnClickListener { findNavController().popBackStack() }

        listenOtherStatus()

    }

    private fun listenOtherStatus() {

        if (otherUid.isEmpty()) return

        statusRegistration = PresenceManager.statusRef(otherUid)
            .addSnapshotListener { value, error ->
                if (error == null && value != null) {
                    lastStatus = value.toObject(UserStatus::class.java)
                    updateStatusUi()
                }
            }

        statusHandler.post(statusRefresh) // periodic re-evaluation (handles app-killed case)

    }

    private fun updateStatusUi() {

        val status = lastStatus
        if (status == null) {
            bnd.tvChatStatus.text = ""
            return
        }

        val now = System.currentTimeMillis()
        val isOnline = status.online && (now - status.lastSeen) < 60_000 // fresh within 1 min

        bnd.tvChatStatus.text = if (isOnline) {
            getString(R.string.status_online)
        } else {
            val ago = DateUtils.getRelativeTimeSpanString(
                status.lastSeen, now, DateUtils.MINUTE_IN_MILLIS
            )
            getString(R.string.status_last_seen, ago)
        }

    }

    private fun sendMessage() {

        val text = bnd.etMessage.text.toString().trim()
        if (text.isEmpty() || otherUid.isEmpty()) return

        val now = System.currentTimeMillis()

        // sentAt is left null on purpose -> Firestore sets it to the SERVER time,
        // so message order is consistent across devices (no clock-skew bug).
        // attach the product to the first message only, so the seller knows what it's about
        val message = ChatMessage(senderId = myUid, text = text, productJson = pendingProductJson)
        pendingProductJson = null

        val chat = Chat(
            chatId = chatId,
            participants = listOf(myUid, otherUid),
            names = mapOf(myUid to myName, otherUid to otherName),
            lastMessage = text,
            updatedAt = now
        )

        viewModel.send(chat, message)
        bnd.etMessage.setText("")

    }

    override fun onDestroyView() {
        super.onDestroyView()
        statusRegistration?.remove()
        statusHandler.removeCallbacks(statusRefresh)
    }

    private fun openProduct(productJson: String) {
        findNavController().navigate(
            R.id.action_chatFragment_to_productDetailsFragment,
            Bundle().apply {
                putString(Constants.PRODUCT_MODEL_NAME, productJson)
            }
        )
    }

}

