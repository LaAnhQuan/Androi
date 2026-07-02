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

    fun onSaveClicked(
        title: String,
        priceText: String,
        description: String,
        image: String,
        category: String,
        stockText: String
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

        val product = Product(
            category = if (category.isBlank()) "other" else category,
            description = description,
            image = image,
            price = price,
            title = title,
            sellerName = sellerName,
            stock = stock
        )

        productRepository.addProduct(product)
            .addOnSuccessListener {
                _addProductLiveData.value = DataState.Success(true)
            }
            .addOnFailureListener { e ->
                _addProductLiveData.value = DataState.Error(e.message ?: "Error")
            }

    }

}
