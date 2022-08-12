package com.example.cosc345project.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.cosc345.shared.models.SaleType
import com.example.cosc345project.models.SearchablePricingInformation
import com.example.cosc345project.models.SearchableProduct
import com.example.cosc345project.models.SearchableRetailerProductInformation
import com.example.cosc345project.paging.FirebaseProductsPagingSource
import com.example.cosc345project.paging.ProductsSearchPagingSource
import com.example.cosc345project.repository.RetailersRepository
import com.example.cosc345project.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    private val retailersRepository: RetailersRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            searchRepository.initialise()
        }
    }

    val searchLiveData: MutableState<Flow<PagingData<SearchableProduct>>> =
        mutableStateOf(
            getFirebaseState()
        )

    val loading = mutableStateOf(false)
    val searchQuery = mutableStateOf("")

    fun query(query: String = "") {
        loading.value = true
        viewModelScope.launch {
            if (searchRepository.isInitialized.value && searchRepository.isAny()) {
                searchLiveData.value = Pager(PagingConfig(pageSize = 10)) {
                    ProductsSearchPagingSource(searchRepository, query)
                }
                    .flow
                    .map {
                        loading.value = false
                        it
                    }
                    .cachedIn(viewModelScope)
            } else {
                searchLiveData.value = getFirebaseState(query)
            }
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
                    .minBy { getPriceFromInformation(it.first, it.second) }

            val lowestPrice =
                getPriceFromInformation(lowestPricePair.first, lowestPricePair.second).toString()

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

    private fun getPriceFromInformation(
        info: SearchablePricingInformation,
        productInformation: SearchableRetailerProductInformation
    ): Int {
        var price =
            if (info.discountPrice == null || info.price?.let { it < info.discountPrice } == true) {
                info.price
            } else {
                info.discountPrice
            }

        if (productInformation.saleType == SaleType.WEIGHT) {
            price = (price!! / (productInformation.weight!!.toDouble() / 1000)).roundToInt()
        }

        return price!!
    }
}