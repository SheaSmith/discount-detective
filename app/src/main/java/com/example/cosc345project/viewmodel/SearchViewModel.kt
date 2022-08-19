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
import com.example.cosc345project.paging.AppSearchProductsPagingSource
import com.example.cosc345project.paging.FirebaseProductsPagingSource
import com.example.cosc345project.repository.RetailersRepository
import com.example.cosc345project.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    private val retailersRepository: RetailersRepository
) : ViewModel() {

    val searchQuery = MutableStateFlow("")
    val suggestions = MutableStateFlow<List<String>>(listOf())
    val showSuggestions = mutableStateOf(false)
    val retailers = MutableStateFlow<Map<String, Retailer>>(mapOf())

    init {
        viewModelScope.launch {
            searchRepository.initialise()
        }

        viewModelScope.launch {
            retailers.value = retailersRepository.getRetailers()
        }
    }

    val searchLiveData: MutableState<Flow<PagingData<Pair<String, Product>>>> =
        mutableStateOf(
            getFirebaseState()
        )

    val loading = mutableStateOf(false)

    val hasIndexed = searchRepository.hasIndexedBefore().asLiveData()

    fun query() {
        loading.value = true
        viewModelScope.launch {
            if (searchRepository.isInitialized.value && searchRepository.hasIndexedBefore()
                    .first()
            ) {
                searchLiveData.value = Pager(PagingConfig(pageSize = 10)) {
                    AppSearchProductsPagingSource(searchRepository, searchQuery.value)
                }
                    .flow
                    .map {
                        loading.value = false
                        it
                    }
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
        return Pager(PagingConfig(pageSize = 10)) {
            FirebaseProductsPagingSource(searchRepository, query)
        }
            .flow
            .map {
                loading.value = false
                it
            }
            .cachedIn(viewModelScope)
    }

    fun setQuery(query: String, runSearch: Boolean = false) {
        searchQuery.value = query
        querySuggestions()

        if (runSearch)
            query()
    }
}