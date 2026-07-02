package com.shopping.app.ui.chat.adapter

import android.content.res.ColorStateList
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shopping.app.R
import com.shopping.app.data.model.ChatMessage
import com.shopping.app.data.model.Product
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessageAdapter(
    private var messages: List<ChatMessage>,
    private val myUid: String,
    private val onProductClick: (productJson: String) -> Unit
) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    private val timeFormat = SimpleDateFormat("HH:mm dd/MM", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount(): Int = messages.size

    fun update(newMessages: List<ChatMessage>) {
        messages = newMessages
        notifyDataSetChanged()
    }

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val row: LinearLayout = itemView.findViewById(R.id.llMessageRow)
        private val bubble: LinearLayout = itemView.findViewById(R.id.llBubble)
        private val tv: TextView = itemView.findViewById(R.id.tvMessage)
        private val llProduct: LinearLayout = itemView.findViewById(R.id.llProduct)
        private val ivProduct: ImageView = itemView.findViewById(R.id.ivProduct)
        private val tvProduct: TextView = itemView.findViewById(R.id.tvProduct)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)

        fun bind(message: ChatMessage) {

            tv.text = message.text
            val date = message.sentAt?.toDate()
            tvTime.text = if (date != null) timeFormat.format(date) else ""

            val isMine = message.senderId == myUid
            row.gravity = if (isMine) Gravity.END else Gravity.START

            val bgColor = if (isMine) R.color.main_color else android.R.color.darker_gray
            bubble.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(itemView.context, bgColor)
            )

            // product card (only when the message references a product)
            val json = message.productJson
            if (!json.isNullOrBlank()) {
                val product = runCatching { Product.fromJson(json) }.getOrNull()
                if (product != null) {
                    llProduct.visibility = View.VISIBLE
                    tvProduct.text = product.title
                    Glide.with(itemView.context).load(product.image).into(ivProduct)
                    llProduct.setOnClickListener { onProductClick(json) }
                } else {
                    llProduct.visibility = View.GONE
                }
            } else {
                llProduct.visibility = View.GONE
                llProduct.setOnClickListener(null)
            }

        }

    }

}
