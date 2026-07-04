package com.shopping.app.ui.main.product

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.shopping.app.R
import com.shopping.app.data.model.Banner
import com.shopping.app.data.model.DataState
import com.shopping.app.data.model.Product
import com.shopping.app.data.preference.UserPref
import com.shopping.app.data.repository.banner.BannerRepositoryImpl
import com.shopping.app.data.repository.category.CategoryRepositoryImpl
import com.shopping.app.data.repository.product.ProductRepositoryImpl
import com.shopping.app.databinding.FragmentProductBinding
import com.shopping.app.ui.loadingprogress.LoadingProgressBar
import com.shopping.app.ui.main.product.adapter.BannerAdapter
import com.shopping.app.ui.main.product.adapter.HomeCategoryAdapter
import com.shopping.app.ui.main.product.adapter.ProductAdapter
import com.shopping.app.ui.main.product.viewmodel.ProductViewModel
import com.shopping.app.ui.main.product.viewmodel.ProductViewModelFactory
import com.shopping.app.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductFragment : Fragment() {

    private lateinit var bnd: FragmentProductBinding
    private lateinit var productAdapter: ProductAdapter
    private lateinit var loadingProgressBar: LoadingProgressBar

    private var allProducts: List<Product> = emptyList()
    private lateinit var bannerAdapter: BannerAdapter
    private val bannerHandler = Handler(Looper.getMainLooper())
    private val bannerRunnable = object : Runnable {
        override fun run() {
            if (bannerAdapter.itemCount > 1) {
                val next = (bnd.bannerPager.currentItem + 1) % bannerAdapter.itemCount
                bnd.bannerPager.setCurrentItem(next, true)
            }
            bannerHandler.postDelayed(this, 3500)
        }
    }
    private val viewModel by viewModels<ProductViewModel> {
        ProductViewModelFactory(
            ProductRepositoryImpl()
        )
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bnd = DataBindingUtil.inflate(inflater, R.layout.fragment_product, container, false)
        return bnd.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingProgressBar = LoadingProgressBar(requireContext())

        setupBanner()
        setupCategoryRow()
        setupAddProductButton()

        viewModel.productLiveData.observe(viewLifecycleOwner){

            when (it) {
                is DataState.Success -> {
                    loadingProgressBar.hide()
                    it.data?.let { safeData ->

                        allProducts = safeData
                        showProducts(safeData)

                    } ?: run {
                        Snackbar.make(bnd.root, getString(R.string.no_data), Snackbar.LENGTH_LONG).show()
                    }
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

    private fun showProducts(list: List<Product>) {
        productAdapter = ProductAdapter(requireContext(), list, findNavController())
        bnd.gridViewProduct.adapter = productAdapter
    }

    // horizontal category quick-filter row (Shopee-style)
    private fun setupCategoryRow() {

        bnd.rvHomeCategories.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        CategoryRepositoryImpl().getCategories()
            .addOnSuccessListener { snapshot ->
                val cats = snapshot.documents.mapNotNull { it.getString("name") }.sorted()
                val withAll = listOf(getString(R.string.all)) + cats
                bnd.rvHomeCategories.adapter = HomeCategoryAdapter(withAll) { selected ->
                    if (selected == getString(R.string.all)) showProducts(allProducts)
                    else showProducts(allProducts.filter { it.category == selected })
                }
            }

    }

    // Auto-sliding banner carousel with dot indicators (Shopee-style)
    private fun setupBanner() {

        bannerAdapter = BannerAdapter(emptyList())
        bnd.bannerPager.adapter = bannerAdapter
        TabLayoutMediator(bnd.bannerDots, bnd.bannerPager) { _, _ -> }.attach()

        BannerRepositoryImpl().getBanners()
            .addOnSuccessListener { snapshot ->
                val banners = snapshot.toObjects(Banner::class.java)
                    .sortedBy { it.order ?: 0 }
                bannerAdapter.update(banners)
            }

    }

    override fun onResume() {
        super.onResume()
        bannerHandler.postDelayed(bannerRunnable, 3500)
    }

    override fun onPause() {
        super.onPause()
        bannerHandler.removeCallbacks(bannerRunnable)
    }

    // Show the "+" button only for seller / admin, then open the Add Product screen
    private fun setupAddProductButton(){

        val userPref = UserPref(requireContext())
        CoroutineScope(Dispatchers.Main).launch {

            val role = userPref.getRole()
            if (role == Constants.ROLE_SELLER || role == Constants.ROLE_ADMIN) {
                bnd.fabAddProduct.visibility = VISIBLE
            } else {
                bnd.fabAddProduct.visibility = GONE
            }

        }

        bnd.fabAddProduct.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addProductFragment)
        }

    }

}