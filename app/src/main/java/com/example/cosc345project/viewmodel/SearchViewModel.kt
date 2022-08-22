package com.example.cosc345project.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.cosc345.shared.models.Product
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345project.models.RetailerProductInfo
import com.example.cosc345project.exceptions.NoInternetException
import com.example.cosc345project.paging.AppSearchProductsPagingSource
import com.example.cosc345project.paging.FirebaseProductsPagingSource
import com.example.cosc345project.repository.RetailersRepository
import com.example.cosc345project.repository.SearchRepository
import com.example.cosc345project.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    private val retailersRepository: RetailersRepository,
    private val productRepository: ProductRepository
) : ViewModel() {

    val searchQuery = MutableStateFlow("")
    val suggestions = MutableStateFlow<List<String>>(listOf())
    val showSuggestions = mutableStateOf(false)
    val retailers = MutableStateFlow<Map<String, Retailer>>(mapOf())
    val noInternet = mutableStateOf(false)

    val searchLiveData: MutableState<Flow<PagingData<Pair<String, Product>>>> =
        mutableStateOf(
            getFirebaseState()
        )

    init {
        viewModelScope.launch {
            searchRepository.initialise()
        }

        viewModelScope.launch {
            retailers.value = retailersRepository.getRetailers()
        }
    }

    val hasIndexed = searchRepository.hasIndexedBefore().asLiveData()

    fun query() {
        noInternet.value = false
        viewModelScope.launch {
            if (searchRepository.isInitialized.value && searchRepository.hasIndexedBefore()
                    .first()
            ) {
                searchLiveData.value = Pager(PagingConfig(pageSize = 10)) {
                    AppSearchProductsPagingSource(searchRepository, searchQuery.value)
                }
                    .flow
                    .cachedIn(viewModelScope)
            } else {
                searchLiveData.value = getFirebaseState(searchQuery.value)
            }
        }
    }

    private fun querySuggestions() {
        viewModelScope.launch {
            if (searchRepository.isInitialized.value && searchRepository.hasIndexedBefore().first())
                suggestions.value = searchRepository.searchSuggestions(searchQuery.value)
        }
    }

    private fun getFirebaseState(query: String = ""): Flow<PagingData<Pair<String, Product>>> {
        return try {
            Pager(PagingConfig(pageSize = 10)) {
                FirebaseProductsPagingSource(searchRepository, query)
            }
                .flow
                .cachedIn(viewModelScope)
        } catch (e: NoInternetException) {
            noInternet.value = true
            flowOf()
        }
    }

    fun setQuery(query: String, runSearch: Boolean = false) {
        searchQuery.value = query
        querySuggestions()

        if (runSearch)
            query()
    }

    fun addToShoppingList(productId: String, retailerProductInfoId: String, storeId: String, quantity: Int) {
        viewModelScope.launch {
            productRepository.insert(RetailerProductInfo(productId, retailerProductInfoId, storeId, quantity))
        }
    }
}