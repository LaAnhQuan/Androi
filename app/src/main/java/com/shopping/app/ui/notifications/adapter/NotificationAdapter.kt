package com.shopping.app.ui.notifications.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shopping.app.R
import com.shopping.app.data.model.Notification
import java.text.SimpleDateFormat
import java.util.Locale

class NotificationAdapter(
    private var items: List<Notification>,
    private val onClick: (Notification) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.NotifViewHolder>() {

    private val dateFormat = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotifViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        return NotifViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotifViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class NotifViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvTitle: TextView = itemView.findViewById(R.id.tvNotifTitle)
        private val tvBody: TextView = itemView.findViewById(R.id.tvNotifBody)
        private val tvTime: TextView = itemView.findViewById(R.id.tvNotifTime)

        fun bind(n: Notification) {
            tvTitle.text = n.title
            tvBody.text = n.body
            val date = n.createdAt?.toDate()
            tvTime.text = if (date != null) dateFormat.format(date) else ""
            itemView.setOnClickListener { onClick(n) }
        }

    }

}
