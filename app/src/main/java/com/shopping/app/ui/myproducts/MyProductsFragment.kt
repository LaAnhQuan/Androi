package com.shopping.app.ui.myproducts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.shopping.app.R
import com.shopping.app.data.model.DataState
import com.shopping.app.data.model.Product
import com.shopping.app.data.repository.product.ProductRepositoryImpl
import com.shopping.app.databinding.FragmentMyProductsBinding
import com.shopping.app.ui.loadingprogress.LoadingProgressBar
import com.shopping.app.ui.myproducts.adapter.MyProductsAdapter
import com.shopping.app.ui.myproducts.viewmodel.MyProductsViewModel
import com.shopping.app.ui.myproducts.viewmodel.MyProductsViewModelFactory
import com.shopping.app.utils.Constants

class MyProductsFragment : Fragment() {

    private lateinit var bnd: FragmentMyProductsBinding
    private lateinit var loadingProgressBar: LoadingProgressBar
    private val viewModel by viewModels<MyProductsViewModel> {
        MyProductsViewModelFactory(ProductRepositoryImpl())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bnd = DataBindingUtil.inflate(inflater, R.layout.fragment_my_products, container, false)
        return bnd.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingProgressBar = LoadingProgressBar(requireContext())
        bnd.rvMyProducts.layoutManager = LinearLayoutManager(requireContext())
        bnd.ivMyProductsBack.setOnClickListener { findNavController().popBackStack() }

        viewModel.productsLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> loadingProgressBar.show()
                is DataState.Success -> {
                    loadingProgressBar.hide()
                    val products = it.data ?: emptyList()
                    bnd.rvMyProducts.adapter = MyProductsAdapter(
                        products,
                        onEdit = { p -> editProduct(p) },
                        onDelete = { p -> confirmDelete(p) }
                    )
                    bnd.tvMyProductsEmpty.visibility = if (products.isEmpty()) View.VISIBLE else View.GONE
                }
                is DataState.Error -> {
                    loadingProgressBar.hide()
                    Snackbar.make(bnd.root, it.message, Snackbar.LENGTH_LONG).show()
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.loadMyProducts() // refresh after adding/editing
    }

    private fun editProduct(product: Product) {
        findNavController().navigate(
            R.id.action_myProductsFragment_to_addProductFragment,
            Bundle().apply { putString(Constants.EDIT_PRODUCT_JSON, product.toJson()) }
        )
    }

    private fun confirmDelete(product: Product) {
        AlertDialog.Builder(requireContext())
            .setTitle(product.title)
            .setMessage(getString(R.string.delete_product_confirm))
            .setPositiveButton(getString(R.string.delete)) { d, _ ->
                d.dismiss()
                product.id?.let { viewModel.deleteProduct(it) }
                Snackbar.make(bnd.root, getString(R.string.product_deleted), Snackbar.LENGTH_SHORT).show()
            }
            .setNegativeButton(getString(R.string.cancel)) { d, _ -> d.dismiss() }
            .show()
    }

}
