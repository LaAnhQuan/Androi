package com.shopping.app.ui.myproducts.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shopping.app.R
import com.shopping.app.data.model.Product

class MyProductsAdapter(
    private var products: List<Product>,
    private val onEdit: (Product) -> Unit,
    private val onDelete: (Product) -> Unit
) : RecyclerView.Adapter<MyProductsAdapter.MyProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_my_product, parent, false)
        return MyProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size

    inner class MyProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val iv: ImageView = itemView.findViewById(R.id.ivMyProduct)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvMyProductTitle)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvMyProductPrice)
        private val btnEdit: Button = itemView.findViewById(R.id.btnEditProduct)
        private val btnDelete: Button = itemView.findViewById(R.id.btnDeleteProduct)

        fun bind(product: Product) {
            tvTitle.text = product.title
            tvPrice.text = itemView.context.getString(R.string.currency_unit, product.price ?: 0.0)
            Glide.with(itemView.context).load(product.image).into(iv)
            btnEdit.setOnClickListener { onEdit(product) }
            btnDelete.setOnClickListener { onDelete(product) }
        }

    }

}
