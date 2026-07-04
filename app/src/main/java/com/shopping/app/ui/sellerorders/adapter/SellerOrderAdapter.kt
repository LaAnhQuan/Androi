package com.shopping.app.ui.sellerorders.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shopping.app.R
import com.shopping.app.data.model.Order
import com.shopping.app.ui.order.adapter.OrderAdapter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SellerOrderAdapter(
    private var orders: List<Order>,
    private val myUid: String,
    private val onUpdateStatus: (Order) -> Unit
) : RecyclerView.Adapter<SellerOrderAdapter.SellerOrderViewHolder>() {

    private val dateFormat = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SellerOrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return SellerOrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: SellerOrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    override fun getItemCount(): Int = orders.size

    inner class SellerOrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvDate: TextView = itemView.findViewById(R.id.tvOrderDate)
        private val tvItems: TextView = itemView.findViewById(R.id.tvOrderItems)
        private val tvTotal: TextView = itemView.findViewById(R.id.tvOrderTotal)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvOrderStatus)
        private val btnUpdate: Button = itemView.findViewById(R.id.btnUpdateStatus)

        @SuppressLint("SetTextI18n")
        fun bind(order: Order) {

            tvDate.text = dateFormat.format(Date(order.createdAt ?: 0L))
            tvStatus.text = OrderAdapter.statusLabel(order.status)

            // seller can update the order status
            btnUpdate.visibility = View.VISIBLE
            btnUpdate.setOnClickListener { onUpdateStatus(order) }

            // only this seller's items in the order
            val myItems = (order.items ?: emptyList()).filter { it.sellerId == myUid }

            tvItems.text = myItems.joinToString("\n") { item ->
                val variant = listOfNotNull(
                    item.color?.takeIf { it.isNotBlank() },
                    item.size?.takeIf { it.isNotBlank() }
                ).joinToString(" / ")
                val v = if (variant.isNotBlank()) " ($variant)" else ""
                "• ${item.title}$v  x${item.piece}"
            }

            val subtotal = myItems.sumOf { (it.price ?: 0.0) * (it.piece ?: 0) }
            tvTotal.text = itemView.context.getString(R.string.order_total, subtotal)

        }

    }

}
