package com.shopping.app.ui.order.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shopping.app.R
import com.shopping.app.data.model.Order
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderAdapter(private var orders: List<Order>) :
    RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    private val dateFormat = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    override fun getItemCount(): Int = orders.size

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvDate: TextView = itemView.findViewById(R.id.tvOrderDate)
        private val tvItems: TextView = itemView.findViewById(R.id.tvOrderItems)
        private val tvTotal: TextView = itemView.findViewById(R.id.tvOrderTotal)

        @SuppressLint("SetTextI18n")
        fun bind(order: Order) {

            tvDate.text = dateFormat.format(Date(order.createdAt ?: 0L))

            val items = order.items ?: emptyList()
            tvItems.text = items.joinToString("\n") { "• ${it.title}  x${it.piece}" }

            tvTotal.text = itemView.context.getString(R.string.order_total, order.total ?: 0.0)

        }

    }

}
