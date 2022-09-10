package io.github.sheasmith.discountdetective.viewmodel

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
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.sheasmith.discountdetective.exceptions.NoInternetException
import io.github.sheasmith.discountdetective.models.RetailerProductInfo
import io.github.sheasmith.discountdetective.paging.AppSearchProductsPagingSource
import io.github.sheasmith.discountdetective.paging.FirebaseProductsPagingSource
import io.github.sheasmith.discountdetective.repository.ProductRepository
import io.github.sheasmith.discountdetective.repository.RetailersRepository
import io.github.sheasmith.discountdetective.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * The viewmodel used for backing the search page.
 *
 * @param searchRepository The search repository, which handles searching items.
 * @param retailersRepository The retailers repository, which handles getting retailers.
 * @param productRepository The products repository, which handles adding items to the shopping list.
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    private val retailersRepository: RetailersRepository,
    private val productRepository: ProductRepository
) : ViewModel() {

    /**
     * The current search query.
     */
    val searchQuery = MutableStateFlow("")

    /**
     * The current search suggestions for the current query.
     */
    val suggestions = MutableStateFlow<List<String>>(listOf())

    /**
     * Whether suggestions should be shown or not.
     */
    val showSuggestions = mutableStateOf(false)

    /**
     * The retailers currently stored in Firebase.
     */
    val retailers = MutableStateFlow<Map<String, Retailer>>(mapOf())

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