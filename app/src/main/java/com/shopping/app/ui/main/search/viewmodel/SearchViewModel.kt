package com.shopping.app.ui.main.search.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shopping.app.data.model.CategoryModel
import com.shopping.app.data.model.DataState
import com.shopping.app.data.model.Product
import com.shopping.app.data.repository.category.CategoryRepository
import com.shopping.app.data.repository.search.SearchRepository

class SearchViewModel(
    private val searchRepository: SearchRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private var productList: List<Product> = listOf()
    private var categoryList: List<CategoryModel> = listOf()

    private var _searchLiveData = MutableLiveData<DataState<List<Product>?>>()
    val searchLiveData: LiveData<DataState<List<Product>?>>
        get() = _searchLiveData

    private var _categoryLiveData = MutableLiveData<DataState<List<CategoryModel>?>>()
    val categoryLiveData: LiveData<DataState<List<CategoryModel>?>>
        get() = _categoryLiveData

    init {
        getCategories()
        getProducts()
    }

    // categories now come from the dedicated "categories" collection (standardized)
    private fun getCategories() {

        _categoryLiveData.postValue(DataState.Loading())
        categoryRepository.getCategories()
            .addOnSuccessListener { snapshot ->
                categoryList = snapshot.documents
                    .mapNotNull { it.getString("name") }
                    .sorted()
                    .map { CategoryModel(it, false) }
                _categoryLiveData.postValue(DataState.Success(categoryList))
            }
            .addOnFailureListener { e ->
                _categoryLiveData.postValue(DataState.Error(e.message.toString()))
            }

    }

    private fun getProducts() {

        _searchLiveData.postValue(DataState.Loading())
        searchRepository.getProducts()
            .addOnSuccessListener { snapshot ->
                productList = snapshot.toObjects(Product::class.java)
                _searchLiveData.postValue(DataState.Success(productList))
            }
            .addOnFailureListener { e ->
                _searchLiveData.postValue(DataState.Error(e.message.toString()))
            }

    }

    fun getProductsByCategoryCheck(categoryModel: CategoryModel) {

        val isSelectedCategory = categoryModel.isSelected
        categoryList.map {
            if (isSelectedCategory) it.isSelected = false
            else it.isSelected = it.categoryName == categoryModel.categoryName
        }

        _categoryLiveData.postValue(DataState.Success(categoryList))

        if (isSelectedCategory) _searchLiveData.postValue(DataState.Success(productList))
        else getProductsByCategory(categoryModel)

    }

    private fun getProductsByCategory(categoryModel: CategoryModel) {

        _searchLiveData.postValue(DataState.Loading())
        searchRepository.getProductsByCategory(categoryModel.categoryName)
            .addOnSuccessListener { snapshot ->
                val filtered = snapshot.toObjects(Product::class.java)
                _searchLiveData.postValue(DataState.Success(filtered))
            }
            .addOnFailureListener { e ->
                _searchLiveData.postValue(DataState.Error(e.message.toString()))
            }

    }

    // advanced filter: price range + sort by price
    fun applyPriceAndSort(minText: String, maxText: String, sort: String) {

        val min = minText.toDoubleOrNull() ?: 0.0
        val max = maxText.toDoubleOrNull() ?: Double.MAX_VALUE

        var list = productList.filter { (it.price ?: 0.0) in min..max }
        list = when (sort) {
            "asc" -> list.sortedBy { it.price ?: 0.0 }
            "desc" -> list.sortedByDescending { it.price ?: 0.0 }
            else -> list
        }

        _searchLiveData.postValue(DataState.Success(list))

    }

    fun searchProducts(isSearch: Boolean = false, query: String = "") {

        if (productList.isNotEmpty()) {
            if (isSearch) {
                val searchList = productList.filter {
                    it.title!!.lowercase().contains(query) || it.description!!.lowercase().contains(query)
                }
                _searchLiveData.postValue(DataState.Success(searchList))
            } else {
                _searchLiveData.postValue(DataState.Success(productList))
            }
        } else {
            getProducts()
        }

    }

}
