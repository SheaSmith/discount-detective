package com.example.cosc345project.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.cosc345project.models.SearchableProduct
import com.example.cosc345project.paging.FirebaseProductsPagingSource
import com.example.cosc345project.paging.ProductsSearchPagingSource
import com.example.cosc345project.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
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

        viewModelScope.launch {

        }
    }

    val searchLiveData: MutableState<Flow<PagingData<SearchableProduct>>> =
        mutableStateOf(
            flowOf()
        )

    val loading = mutableStateOf(false)

    fun query(query: String = "") {
        loading.value = true
        viewModelScope.launch {
            if (searchRepository.isAny()) {
                searchLiveData.value = Pager(PagingConfig(pageSize = 50)) {
                    ProductsSearchPagingSource(searchRepository, query)
                }
                    .flow
                    .map {
                        loading.value = false
                        it
                    }
                    .cachedIn(viewModelScope)
            } else {
                searchLiveData.value = Pager(PagingConfig(pageSize = 50)) {
                    FirebaseProductsPagingSource(searchRepository, query)
                }
                    .flow
                    .map {
                        loading.value = false
                        it
                    }
                    .cachedIn(viewModelScope)
            }
        }
    }
}