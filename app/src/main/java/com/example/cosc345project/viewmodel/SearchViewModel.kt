package com.example.cosc345project.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.SaleType
import com.example.cosc345project.extensions.getPrice
import com.example.cosc345project.models.SearchableProduct
import com.example.cosc345project.models.SearchableRetailerProductInformation
import com.example.cosc345project.paging.AppSearchProductsPagingSource
import com.example.cosc345project.paging.FirebaseProductsPagingSource
import com.example.cosc345project.repository.RetailersRepository
import com.example.cosc345project.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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

    val searchLiveData: MutableState<Flow<PagingData<SearchableProduct>>> =
        mutableStateOf(
            getFirebaseState()
        )

    val loading = mutableStateOf(false)

    fun query() {
        loading.value = true
        showSuggestions.value = false
        viewModelScope.launch {
            if (searchRepository.isInitialized.value && searchRepository.hasIndexedBefore()) {
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
            if (searchRepository.isInitialized.value && searchRepository.hasIndexedBefore())
                suggestions.value = searchRepository.searchSuggestions(searchQuery.value)
        }
    }

    private fun getFirebaseState(query: String = ""): Flow<PagingData<SearchableProduct>> {
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

    /**
     * Get the best local price for a particular product.
     *
     * @param product The product to get the price for.
     * @return A pair, with the first item being the dollar component of the price, and the second being the cents and the sale type.
     */
    fun getLocalPrice(product: SearchableProduct): Pair<String, String>? {
        val localPrices = product.information!!.filter { it.local }

        return findLowestPrice(localPrices)
    }

    fun getBestPrice(product: SearchableProduct): Pair<String, String>? {
        val nonLocalPrices = product.information!!.filter { !it.local }

        return findLowestPrice(nonLocalPrices)
    }

    private fun findLowestPrice(products: List<SearchableRetailerProductInformation>): Pair<String, String>? {
        if (products.isNotEmpty()) {
            val lowestPricePair =
                products.flatMap { productInfo -> productInfo.pricing.map { it to productInfo } }
                    .minBy { it.first.getPrice(it.second) }

            val lowestPrice =
                lowestPricePair.first.getPrice(lowestPricePair.second).toString()

            val salePrefix = if (lowestPricePair.second.saleType == SaleType.WEIGHT) {
                "kg"
            } else {
                "ea"
            }

            val dollarComponent = "$${lowestPrice.substring(0, lowestPrice.length - 2)}"
            val centsComponent =
                ".${
                    lowestPrice.substring(
                        lowestPrice.length - 2,
                        lowestPrice.length
                    )
                }/${salePrefix}"

            return Pair(dollarComponent, centsComponent)
        }

        return null
    }


    fun setQuery(query: String, runSearch: Boolean = false) {
        searchQuery.value = query
        querySuggestions()

        if (runSearch)
            query()
    }
}