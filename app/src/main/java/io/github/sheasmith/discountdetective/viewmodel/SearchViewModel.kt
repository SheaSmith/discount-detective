package io.github.sheasmith.discountdetective.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.cosc345.shared.models.Product
import com.example.cosc345.shared.models.Region
import com.example.cosc345.shared.models.Retailer
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.sheasmith.discountdetective.exceptions.NoInternetException
import io.github.sheasmith.discountdetective.models.ShoppingListItem
import io.github.sheasmith.discountdetective.paging.AppSearchProductsPagingSource
import io.github.sheasmith.discountdetective.paging.FirebaseProductsPagingSource
import io.github.sheasmith.discountdetective.repository.PreferencesRepository
import io.github.sheasmith.discountdetective.repository.RetailersRepository
import io.github.sheasmith.discountdetective.repository.SearchRepository
import io.github.sheasmith.discountdetective.repository.ShoppingListRepository
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
 * @param shoppingListRepository The shopping list repository, which handles adding items to the shopping list.
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    private val retailersRepository: RetailersRepository,
    private val shoppingListRepository: ShoppingListRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    /**
     * The current search query.
     */
    val searchQuery: MutableStateFlow<String> = MutableStateFlow("")

    /**
     * The current search suggestions for the current query.
     */
    val suggestions: MutableStateFlow<List<String>> = MutableStateFlow(listOf())

    /**
     * Whether suggestions should be shown or not.
     */
    val showSuggestions: MutableState<Boolean> = mutableStateOf(false)

    /**
     * The retailers currently stored in Firebase.
     */
    val retailers: MutableStateFlow<Map<String, Retailer>> = MutableStateFlow(mapOf())

    /**
     * Users current region.
     */
    val region = preferencesRepository.getRegion()

    /**
     * The live data for the paged search results.
     */
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

    /**
     * Whether the app search indexing has finished or not.
     */
    val hasIndexed: LiveData<Boolean> = searchRepository.hasIndexedBefore().asLiveData()

    /**
     * Run a query on either firebase or app search.
     */
    fun query() {
        viewModelScope.launch {
            if (searchRepository.isInitialized.value && searchRepository.hasIndexedBefore()
                    .first()
            ) {
                searchLiveData.value = Pager(PagingConfig(pageSize = 10)) {
                    AppSearchProductsPagingSource(
                        searchRepository,
                        "${searchQuery.value} \"${preferencesRepository.getRegion()}\""
                    )
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
                FirebaseProductsPagingSource(
                    searchRepository,
                    query,
                    region.value ?: Region.DUNEDIN
                )
            }
                .flow
                .cachedIn(viewModelScope)
        } catch (e: NoInternetException) {
            flowOf()
        }
    }

    /**
     * Set the current search query.
     *
     * @param query The query to set the search query to.
     * @param runSearch Whether the search should be run or not.
     */
    fun setQuery(query: String, runSearch: Boolean = false) {
        searchQuery.value = query
        querySuggestions()

        if (runSearch)
            query()
    }

    /**
     * Add the product to the shopping list.
     *
     * @param productId The ID of the product to add to the shopping list.
     * @param retailerProductInfoId The ID of the retailer product info the user has selected.
     * @param storeId The ID of the store whose price the user has selected.
     * @param quantity The quantity picked by the user.
     */
    fun addToShoppingList(
        productId: String,
        retailerProductInfoId: String,
        storeId: String,
        quantity: Int
    ) {
        viewModelScope.launch {
            shoppingListRepository.addToShoppingList(
                ShoppingListItem(
                    productId,
                    retailerProductInfoId,
                    storeId,
                    quantity
                )
            )
        }
    }
}