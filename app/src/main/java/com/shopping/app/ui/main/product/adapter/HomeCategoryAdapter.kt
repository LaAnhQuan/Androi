package com.shopping.app.ui.main.product.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.shopping.app.R

class HomeCategoryAdapter(
    private val categories: List<String>,   // includes "All" at index 0
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<HomeCategoryAdapter.CategoryViewHolder>() {

    private var selectedIndex = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_home_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position], position == selectedIndex)
    }

    override fun getItemCount(): Int = categories.size

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tv = itemView as TextView

        fun bind(name: String, selected: Boolean) {
            tv.text = name

            if (selected) {
                tv.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(itemView.context, R.color.shopee_orange)
                )
                tv.setTextColor(Color.WHITE)
            } else {
                tv.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(itemView.context, R.color.bg_light)
                )
                tv.setTextColor(ContextCompat.getColor(itemView.context, R.color.black_secondary))
            }

            tv.setOnClickListener {
                val old = selectedIndex
                selectedIndex = adapterPosition
                notifyItemChanged(old)
                notifyItemChanged(selectedIndex)
                onClick(name)
            }
        }
    }

}
