package com.shopping.app.ui.addproduct.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shopping.app.data.model.DataState
import com.shopping.app.data.model.Product
import com.shopping.app.data.repository.product.ProductRepository

class AddProductViewModel(private val productRepository: ProductRepository) : ViewModel() {

    private val _addProductLiveData = MutableLiveData<DataState<Boolean>>()
    val addProductLiveData: LiveData<DataState<Boolean>>
        get() = _addProductLiveData

    // set by the fragment from the logged-in user's username
    var sellerName: String = ""

    // set when editing an existing product (null = adding a new one)
    var editingProduct: Product? = null

    fun onSaveClicked(
        title: String,
        priceText: String,
        description: String,
        image: String,
        category: String,
        stockText: String,
        colorsText: String = "",
        sizesText: String = ""
    ) {

        _addProductLiveData.value = DataState.Loading()

        if (title.isBlank() || priceText.isBlank() || image.isBlank()) {
            _addProductLiveData.value = DataState.Error("product_fields_empty")
            return
        }

        val price = priceText.toDoubleOrNull()
        if (price == null) {
            _addProductLiveData.value = DataState.Error("Invalid price")
            return
        }

        val stock = stockText.toIntOrNull() ?: 0
        val colors = parseList(colorsText)
        val sizes = parseList(sizesText)
        val editing = editingProduct

        if (editing != null) {

            // update: keep id / sellerId / sellerName
            editing.title = title
            editing.price = price
            editing.description = description
            editing.image = image
            editing.category = if (category.isBlank()) "other" else category
            editing.stock = stock
            editing.colors = colors
            editing.sizes = sizes

            productRepository.updateProduct(editing)
                .addOnSuccessListener { _addProductLiveData.value = DataState.Success(true) }
                .addOnFailureListener { e -> _addProductLiveData.value = DataState.Error(e.message ?: "Error") }

        } else {

            val product = Product(
                category = if (category.isBlank()) "other" else category,
                description = description,
                image = image,
                price = price,
                title = title,
                sellerName = sellerName,
                stock = stock,
                colors = colors,
                sizes = sizes
            )

            productRepository.addProduct(product)
                .addOnSuccessListener { _addProductLiveData.value = DataState.Success(true) }
                .addOnFailureListener { e -> _addProductLiveData.value = DataState.Error(e.message ?: "Error") }

        }

    }

    // "Red, Blue , Black" -> ["Red","Blue","Black"]
    private fun parseList(text: String): List<String> {
        return text.split(",").map { it.trim() }.filter { it.isNotBlank() }
    }

}
