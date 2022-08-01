package com.example.cosc345project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cosc345project.models.SearchableProduct
import com.example.cosc345project.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            searchRepository.initialise()
        }
    }

    private val _searchLiveData: MutableLiveData<List<SearchableProduct>> =
        MutableLiveData(mutableListOf())
    val searchLiveData: LiveData<List<SearchableProduct>> = _searchLiveData

    fun query(query: String = "") {
        viewModelScope.launch {
            val result = if (true) {
                searchRepository.queryProducts(query)
                    .map { it.getDocument(SearchableProduct::class.java) }
            } else
                searchRepository.queryProductsFirebase(query)
            _searchLiveData.postValue(result)
        }
    }
}