package com.shopping.app.ui.productdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.shopping.app.R
import com.shopping.app.data.model.DataState
import com.shopping.app.data.model.Product
import com.shopping.app.data.model.Review
import com.shopping.app.data.preference.UserPref
import com.shopping.app.data.repository.basket.BasketRepositoryImpl
import com.shopping.app.data.repository.review.ReviewRepositoryImpl
import com.shopping.app.databinding.FragmentProductDetailsBinding
import com.shopping.app.ui.loadingprogress.LoadingProgressBar
import com.shopping.app.ui.productdetail.adapter.ReviewAdapter
import com.shopping.app.ui.productdetail.viewmodel.ProductDetailViewModel
import com.shopping.app.ui.productdetail.viewmodel.ProductDetailViewModelFactory
import com.shopping.app.ui.productdetail.viewmodel.ReviewViewModel
import com.shopping.app.ui.productdetail.viewmodel.ReviewViewModelFactory
import com.shopping.app.utils.Constants
import com.shopping.app.utils.Constants.PRODUCT_MODEL_NAME
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductDetailsFragment : Fragment() {

    private lateinit var bnd: FragmentProductDetailsBinding
    private lateinit var loadingProgressBar: LoadingProgressBar
    private var currentProduct: Product? = null
    private var myName: String = "User"

    private val reviewViewModel by viewModels<ReviewViewModel> {
        ReviewViewModelFactory(ReviewRepositoryImpl())
    }
    private lateinit var reviewAdapter: ReviewAdapter
    private val viewModel by viewModels<ProductDetailViewModel> {
        ProductDetailViewModelFactory(
            BasketRepositoryImpl()
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bnd = DataBindingUtil.inflate(inflater, R.layout.fragment_product_details, container, false)
        return bnd.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init(){

        getBundleArguments()
        bnd.viewModel = viewModel
        bnd.productDetailsFragment = this

        loadingProgressBar = LoadingProgressBar(requireContext())

        viewModel.addBasketLiveData.observe(viewLifecycleOwner){

            when (it) {
                is DataState.Success -> {
                    loadingProgressBar.hide()
                    Toast.makeText(context, getString(R.string.product_added_basket_message), Toast.LENGTH_SHORT).show()
                }
                is DataState.Error -> {
                    loadingProgressBar.hide()
                    Snackbar.make(bnd.root, it.message, Snackbar.LENGTH_LONG).show()
                }
                is DataState.Loading -> {
                    loadingProgressBar.show()
                }
            }

        }

    }

    private fun getBundleArguments(){

        arguments?.let {

            val productData = it.getString(PRODUCT_MODEL_NAME)

            // null state check of data
            productData?.let { safeData ->

                val product = Product.fromJson(safeData)
                currentProduct = product
                bnd.dataHolder = product

                setupStock(product)
                setupReviews(product)

                viewModel.productCountLiveData.observe(viewLifecycleOwner){ value ->
                    bnd.basketCount = value
                }

            }
        }

    }

    // show stock; disable buying when out of stock
    private fun setupStock(product: Product) {
        val stock = product.stock ?: 0
        if (stock <= 0) {
            bnd.tvStock.text = getString(R.string.out_of_stock)
            bnd.tvStock.setTextColor(resources.getColor(android.R.color.holo_red_dark, null))
            bnd.btnAddBasket.isEnabled = false
            bnd.btnAddBasket.alpha = 0.5f
        } else {
            bnd.tvStock.text = getString(R.string.in_stock, stock)
        }
    }

    // reviews: load list + prepare my display name for submitting
    private fun setupReviews(product: Product) {

        reviewAdapter = ReviewAdapter(emptyList())
        bnd.rvReviews.layoutManager = LinearLayoutManager(requireContext())
        bnd.rvReviews.adapter = reviewAdapter

        val userPref = UserPref(requireContext())
        CoroutineScope(Dispatchers.Main).launch { myName = userPref.getUsername().ifBlank { "User" } }

        reviewViewModel.reviewsLiveData.observe(viewLifecycleOwner) { list ->
            reviewAdapter.update(list)
            bnd.tvNoReviews.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }

        product.id?.let { reviewViewModel.listen(it) }

    }

    fun submitReview() {

        val product = currentProduct ?: return
        val rating = bnd.rbReviewInput.rating
        val comment = bnd.etReviewComment.text.toString().trim()

        if (rating <= 0f || comment.isEmpty()) {
            Toast.makeText(context, getString(R.string.review_empty), Toast.LENGTH_SHORT).show()
            return
        }

        val review = Review(
            productId = product.id,
            userId = FirebaseAuth.getInstance().uid,
            userName = myName,
            rating = rating,
            comment = comment
        )
        reviewViewModel.submit(review)

        bnd.etReviewComment.setText("")
        bnd.rbReviewInput.rating = 5f
        Toast.makeText(context, getString(R.string.review_added), Toast.LENGTH_SHORT).show()

    }

    // called by the "Add Basket" button — slides up a bottom sheet to pick color/size/quantity
    fun addToBasket() {
        val product = currentProduct ?: return
        if ((product.stock ?: 0) <= 0) return
        VariantBottomSheet.newInstance(product).show(childFragmentManager, "variant")
    }

    fun goBack(){
        findNavController().popBackStack()
    }

    fun chatWithSeller(){

        val sellerId = currentProduct?.sellerId

        // seeded/demo products have sellerId "seed" (no real user to chat with)
        if (sellerId.isNullOrBlank() || sellerId == "seed") {
            Toast.makeText(context, "This product has no seller to chat with.", Toast.LENGTH_SHORT).show()
            return
        }

        // can't chat with yourself (your own product)
        if (sellerId == FirebaseAuth.getInstance().uid) {
            Toast.makeText(context, "This is your own product.", Toast.LENGTH_SHORT).show()
            return
        }

        val sellerName = currentProduct?.sellerName?.takeIf { it.isNotBlank() } ?: "Seller"

        findNavController().navigate(
            R.id.action_productDetailsFragment_to_chatFragment,
            Bundle().apply {
                putString(Constants.CHAT_OTHER_UID, sellerId)
                putString(Constants.CHAT_OTHER_NAME, sellerName)
                putString(Constants.CHAT_PRODUCT_JSON, currentProduct?.toJson())
            }
        )

    }

}