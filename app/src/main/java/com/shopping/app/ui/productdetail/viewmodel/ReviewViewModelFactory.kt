package com.shopping.app.ui.productdetail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shopping.app.data.repository.review.ReviewRepository

class ReviewViewModelFactory(private val reviewRepository: ReviewRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ReviewViewModel(reviewRepository) as T
    }
}
