package com.shopping.app.ui.addproduct

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.shopping.app.R
import com.shopping.app.data.model.DataState
import com.shopping.app.data.repository.product.ProductRepositoryImpl
import com.shopping.app.databinding.FragmentAddProductBinding
import com.shopping.app.ui.addproduct.viewmodel.AddProductViewModel
import com.shopping.app.ui.addproduct.viewmodel.AddProductViewModelFactory
import com.shopping.app.ui.loadingprogress.LoadingProgressBar

class AddProductFragment : Fragment() {

    private lateinit var bnd: FragmentAddProductBinding
    private lateinit var loadingProgressBar: LoadingProgressBar
    private val viewModel by viewModels<AddProductViewModel> {
        AddProductViewModelFactory(ProductRepositoryImpl())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bnd = DataBindingUtil.inflate(inflater, R.layout.fragment_add_product, container, false)
        return bnd.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bnd.viewModel = viewModel
        loadingProgressBar = LoadingProgressBar(requireContext())

        viewModel.addProductLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> loadingProgressBar.show()
                is DataState.Success -> {
                    loadingProgressBar.hide()
                    Toast.makeText(context, getString(R.string.product_added_message), Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
                is DataState.Error -> {
                    loadingProgressBar.hide()
                    val msg = if (it.message == "product_fields_empty")
                        getString(R.string.product_fields_empty) else it.message
                    Snackbar.make(bnd.root, msg, Snackbar.LENGTH_LONG).show()
                }
            }
        }

    }

}
