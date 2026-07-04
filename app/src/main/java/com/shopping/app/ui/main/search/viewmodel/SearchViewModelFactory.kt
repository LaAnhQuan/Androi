package com.shopping.app.ui.main.search.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shopping.app.data.repository.category.CategoryRepository
import com.shopping.app.data.repository.search.SearchRepository

class SearchViewModelFactory(
    private val searchRepository: SearchRepository,
    private val categoryRepository: CategoryRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SearchViewModel(searchRepository, categoryRepository) as T
    }
}
