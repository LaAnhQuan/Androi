package com.shopping.app.ui.productdetail.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shopping.app.R
import com.shopping.app.data.model.Review
import java.text.SimpleDateFormat
import java.util.Locale

class ReviewAdapter(private var reviews: List<Review>) :
    RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    private val dateFormat = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_review, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(reviews[position])
    }

    override fun getItemCount(): Int = reviews.size

    fun update(newReviews: List<Review>) {
        reviews = newReviews
        notifyDataSetChanged()
    }

    inner class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvUser: TextView = itemView.findViewById(R.id.tvReviewUser)
        private val rb: RatingBar = itemView.findViewById(R.id.rbReview)
        private val tvComment: TextView = itemView.findViewById(R.id.tvReviewComment)
        private val tvTime: TextView = itemView.findViewById(R.id.tvReviewTime)

        fun bind(review: Review) {
            tvUser.text = review.userName ?: "User"
            rb.rating = review.rating
            tvComment.text = review.comment
            val date = review.createdAt?.toDate()
            tvTime.text = if (date != null) dateFormat.format(date) else ""
        }
    }

}
