package com.shopping.app.ui.productdetail.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ListenerRegistration
import com.shopping.app.data.model.Review
import com.shopping.app.data.repository.review.ReviewRepository

class ReviewViewModel(private val reviewRepository: ReviewRepository) : ViewModel() {

    private val _reviewsLiveData = MutableLiveData<List<Review>>()
    val reviewsLiveData: LiveData<List<Review>>
        get() = _reviewsLiveData

    private var registration: ListenerRegistration? = null

    fun listen(productId: String) {
        registration?.remove()
        registration = reviewRepository.getReviews(productId)
            .addSnapshotListener { value, error ->
                if (error == null && value != null) {
                    val list = value.toObjects(Review::class.java)
                        .sortedByDescending { it.createdAt?.toDate()?.time ?: 0L }
                    _reviewsLiveData.value = list
                }
            }
    }

    fun submit(review: Review) {
        reviewRepository.addReview(review)
    }

    override fun onCleared() {
        super.onCleared()
        registration?.remove()
    }

}
