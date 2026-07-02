package com.shopping.app.ui.chat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shopping.app.R
import com.shopping.app.data.model.Chat

class ChatListAdapter(
    private var chats: List<Chat>,
    private val myUid: String,
    private val onClick: (otherUid: String, otherName: String) -> Unit
) : RecyclerView.Adapter<ChatListAdapter.ChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(chats[position])
    }

    override fun getItemCount(): Int = chats.size

    fun update(newChats: List<Chat>) {
        chats = newChats
        notifyDataSetChanged()
    }

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val root: LinearLayout = itemView.findViewById(R.id.llChatItem)
        private val tvName: TextView = itemView.findViewById(R.id.tvChatName)
        private val tvLast: TextView = itemView.findViewById(R.id.tvChatLast)

        fun bind(chat: Chat) {

            val otherUid = chat.participants?.firstOrNull { it != myUid } ?: ""
            val otherName = chat.names?.get(otherUid) ?: otherUid

            tvName.text = otherName
            tvLast.text = chat.lastMessage ?: ""

            root.setOnClickListener { onClick(otherUid, otherName) }

        }

    }

}
