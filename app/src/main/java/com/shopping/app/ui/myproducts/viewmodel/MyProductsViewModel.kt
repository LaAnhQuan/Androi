package com.shopping.app.ui.myproducts.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.shopping.app.data.model.DataState
import com.shopping.app.data.model.Product
import com.shopping.app.data.repository.product.ProductRepository

class MyProductsViewModel(private val productRepository: ProductRepository) : ViewModel() {

    private val _productsLiveData = MutableLiveData<DataState<List<Product>?>>()
    val productsLiveData: LiveData<DataState<List<Product>?>>
        get() = _productsLiveData

    fun loadMyProducts() {

        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            _productsLiveData.value = DataState.Error("Not signed in")
            return
        }

        _productsLiveData.postValue(DataState.Loading())
        productRepository.getProductsBySeller(uid)
            .addOnSuccessListener { snapshot ->
                _productsLiveData.postValue(DataState.Success(snapshot.toObjects(Product::class.java)))
            }
            .addOnFailureListener { e ->
                _productsLiveData.postValue(DataState.Error(e.message.toString()))
            }

    }

    fun deleteProduct(productId: String) {
        productRepository.deleteProduct(productId)
            .addOnSuccessListener { loadMyProducts() }
    }

}
