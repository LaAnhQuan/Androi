package com.shopping.app.ui.main.product.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shopping.app.data.model.DataState
import com.shopping.app.data.model.Product
import com.shopping.app.data.repository.product.ProductRepository

class ProductViewModel(private val productRepository: ProductRepository) : ViewModel() {

    private var _productLiveData = MutableLiveData<DataState<List<Product>?>>()
    val productLiveData: LiveData<DataState<List<Product>?>>
        get() = _productLiveData

    init {
        getProducts()
    }

    private fun getProducts(){

        _productLiveData.postValue(DataState.Loading())
        productRepository.getProducts()
            .addOnSuccessListener { snapshot ->
                val products = snapshot.toObjects(Product::class.java)
                if (products.isNotEmpty()) {
                    _productLiveData.postValue(DataState.Success(products))
                } else {
                    _productLiveData.postValue(DataState.Error("Data Empty"))
                }
            }
            .addOnFailureListener { e ->
                _productLiveData.postValue(DataState.Error(e.message.toString()))
            }

    }

}
