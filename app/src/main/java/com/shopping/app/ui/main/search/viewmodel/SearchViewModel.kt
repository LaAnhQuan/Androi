package com.shopping.app.ui.main.search.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shopping.app.data.model.CategoryModel
import com.shopping.app.data.model.DataState
import com.shopping.app.data.model.Product
import com.shopping.app.data.repository.search.SearchRepository

class SearchViewModel(private val searchRepository: SearchRepository) : ViewModel() {

    private var productList: List<Product> = listOf()
    private var categoryList: List<CategoryModel> = listOf()

    private var _searchLiveData = MutableLiveData<DataState<List<Product>?>>()
    val searchLiveData: LiveData<DataState<List<Product>?>>
        get() = _searchLiveData

    private var _categoryLiveData = MutableLiveData<DataState<List<CategoryModel>?>>()
    val categoryLiveData: LiveData<DataState<List<CategoryModel>?>>
        get() = _categoryLiveData

    init {
        getProducts()
    }

    private fun getProducts(){

        _searchLiveData.postValue(DataState.Loading())
        searchRepository.getProducts()
            .addOnSuccessListener { snapshot ->

                productList = snapshot.toObjects(Product::class.java)
                _searchLiveData.postValue(DataState.Success(productList))

                buildCategories()

            }
            .addOnFailureListener { e ->
                _searchLiveData.postValue(DataState.Error(e.message.toString()))
            }

    }

    // Derive the category list from the products already loaded (distinct category names)
    private fun buildCategories(){

        categoryList = productList
            .mapNotNull { it.category }
            .distinct()
            .map { CategoryModel(it, false) }

        _categoryLiveData.postValue(DataState.Success(categoryList))

    }

    fun getProductsByCategoryCheck(categoryModel: CategoryModel){

        val isSelectedCategory = categoryModel.isSelected
        categoryList.map {

            if(isSelectedCategory) it.isSelected = false
            else it.isSelected = it.categoryName == categoryModel.categoryName

        }

        _categoryLiveData.postValue(DataState.Success(categoryList))

        if(isSelectedCategory) _searchLiveData.postValue(DataState.Success(productList))
        else getProductsByCategory(categoryModel)

    }

    private fun getProductsByCategory(categoryModel: CategoryModel){

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

    fun searchProducts(isSearch:Boolean = false, query:String = ""){

        if(productList.isNotEmpty()){

            if(isSearch){

                val searchList = productList.filter {
                    it.title!!.lowercase().contains(query) || it.description!!.lowercase().contains(query)
                }

                _searchLiveData.postValue(DataState.Success(searchList))

            }else{
                _searchLiveData.postValue(DataState.Success(productList))
            }

        }else{
            getProducts()
        }

    }

}
