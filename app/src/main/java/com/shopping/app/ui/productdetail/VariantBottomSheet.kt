package com.shopping.app.ui.productdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.shopping.app.R
import com.shopping.app.data.model.DataState
import com.shopping.app.data.model.Product
import com.shopping.app.data.repository.basket.BasketRepositoryImpl
import com.shopping.app.ui.productdetail.viewmodel.ProductDetailViewModel
import com.shopping.app.ui.productdetail.viewmodel.ProductDetailViewModelFactory
import com.shopping.app.utils.Constants

class VariantBottomSheet : BottomSheetDialogFragment() {

    private val viewModel by viewModels<ProductDetailViewModel> {
        ProductDetailViewModelFactory(BasketRepositoryImpl())
    }

    private lateinit var product: Product

    private lateinit var chipColors: ChipGroup
    private lateinit var chipSizes: ChipGroup

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_variant_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val json = arguments?.getString(Constants.PRODUCT_MODEL_NAME) ?: run { dismiss(); return }
        product = Product.fromJson(json)

        val iv = view.findViewById<ImageView>(R.id.ivSheetImage)
        val tvTitle = view.findViewById<TextView>(R.id.tvSheetTitle)
        val tvPrice = view.findViewById<TextView>(R.id.tvSheetPrice)
        val tvCount = view.findViewById<TextView>(R.id.tvSheetCount)
        val btnReduce = view.findViewById<TextView>(R.id.btnSheetReduce)
        val btnIncrease = view.findViewById<TextView>(R.id.btnSheetIncrease)
        val btnAdd = view.findViewById<Button>(R.id.btnSheetAdd)
        chipColors = view.findViewById(R.id.chipSheetColors)
        chipSizes = view.findViewById(R.id.chipSheetSizes)

        tvTitle.text = product.title
        tvPrice.text = getString(R.string.currency_unit, product.price ?: 0.0)
        Glide.with(this).load(product.image).into(iv)

        // color / size chips
        product.colors?.takeIf { it.isNotEmpty() }?.let {
            view.findViewById<TextView>(R.id.tvSheetColorLabel).visibility = View.VISIBLE
            chipColors.visibility = View.VISIBLE
            addChips(chipColors, it)
        }
        product.sizes?.takeIf { it.isNotEmpty() }?.let {
            view.findViewById<TextView>(R.id.tvSheetSizeLabel).visibility = View.VISIBLE
            chipSizes.visibility = View.VISIBLE
            addChips(chipSizes, it)
        }

        // quantity
        viewModel.productCountLiveData.observe(viewLifecycleOwner) { tvCount.text = it.toString() }
        btnReduce.setOnClickListener { viewModel.reduceCount(tvCount.text.toString().toInt()) }
        btnIncrease.setOnClickListener { viewModel.increaseCount(tvCount.text.toString().toInt()) }

        // add to basket
        viewModel.addBasketLiveData.observe(viewLifecycleOwner) {
            if (it is DataState.Success) {
                Toast.makeText(context, getString(R.string.product_added_basket_message), Toast.LENGTH_SHORT).show()
                dismiss()
            } else if (it is DataState.Error) {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
        }

        btnAdd.setOnClickListener {
            viewModel.checkProduct(product, selectedText(chipColors), selectedText(chipSizes))
        }

    }

    private fun addChips(group: ChipGroup, values: List<String>) {
        values.forEachIndexed { index, value ->
            val chip = Chip(requireContext())
            chip.text = value
            chip.id = View.generateViewId()
            chip.isCheckable = true
            if (index == 0) chip.isChecked = true
            group.addView(chip)
        }
    }

    private fun selectedText(group: ChipGroup): String {
        val id = group.checkedChipId
        if (id == View.NO_ID) return ""
        return group.findViewById<Chip>(id)?.text?.toString() ?: ""
    }

    companion object {
        fun newInstance(product: Product): VariantBottomSheet {
            return VariantBottomSheet().apply {
                arguments = Bundle().apply {
                    putString(Constants.PRODUCT_MODEL_NAME, product.toJson())
                }
            }
        }
    }

}
