package com.shopping.app.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.shopping.app.R
import com.shopping.app.data.repository.chat.ChatRepositoryImpl
import com.shopping.app.databinding.FragmentChatListBinding
import com.shopping.app.ui.chat.adapter.ChatListAdapter
import com.shopping.app.ui.chat.viewmodel.ChatListViewModel
import com.shopping.app.ui.chat.viewmodel.ChatListViewModelFactory
import com.shopping.app.utils.Constants

class ChatListFragment : Fragment() {

    private lateinit var bnd: FragmentChatListBinding
    private val viewModel by viewModels<ChatListViewModel> {
        ChatListViewModelFactory(ChatRepositoryImpl())
    }

    private val myUid: String by lazy { FirebaseAuth.getInstance().uid ?: "" }
    private lateinit var adapter: ChatListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bnd = DataBindingUtil.inflate(inflater, R.layout.fragment_chat_list, container, false)
        return bnd.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ChatListAdapter(emptyList(), myUid) { otherUid, otherName ->
            openChat(otherUid, otherName)
        }
        bnd.rvChats.layoutManager = LinearLayoutManager(requireContext())
        bnd.rvChats.adapter = adapter

        viewModel.chatsLiveData.observe(viewLifecycleOwner) { chats ->
            adapter.update(chats)
            bnd.tvEmpty.visibility = if (chats.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.listenChats(myUid)

        bnd.ivChatListBack.setOnClickListener { findNavController().popBackStack() }

    }

    private fun openChat(otherUid: String, otherName: String) {
        findNavController().navigate(
            R.id.action_chatListFragment_to_chatFragment,
            Bundle().apply {
                putString(Constants.CHAT_OTHER_UID, otherUid)
                putString(Constants.CHAT_OTHER_NAME, otherName)
            }
        )
    }

}
