package com.example.cosc345project.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.cosc345.shared.models.Product
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.RetailerProductInformation
import com.example.cosc345project.models.RetailerProductInfo
import com.example.cosc345project.repository.ProductRepository
import com.example.cosc345project.repository.RetailersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Holds all data associated with screen
 * + setters and getters
 *
 * Responsible for preparing data for the screen.
 * and
 * Transform the data stored from the repo
 * from flow to LiveData to the UI
 * Ensures that everytime data changes in database, UI
 * Automatically updated

 * Specific:
 * need to display a list of products
 *
 */
@HiltViewModel
class ShoppingListViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val retailersRepository: RetailersRepository
) : ViewModel() {

    val allProducts: LiveData<List<RetailerProductInfo>> =
        productRepository.allProducts.asLiveData()

    val retailers = MutableStateFlow<Map<String, Retailer>>(mapOf())

    init {
        viewModelScope.launch {
            retailers.value = retailersRepository.getRetailers()
        }
    }

    /**
     * For a given RetailerProductInfo in dao
     * Return the associated store namze
     *
     * have retailer ID
     *
     * If store name and retailer name same just return store
     */
    fun getStoreName(
        retailerProductInformationID: String,
        storePricingInformationID: String,
        retailers: Map<String, Retailer>,
        productInfo: RetailerProductInformation?
    ): Pair<String?, String?> {
        val retailer = retailers[retailerProductInformationID]
        val store = retailer?.stores?.firstOrNull {
            it.id == storePricingInformationID
        }
        //if retailer weird string use storePicingInfo to index into retailers
        val storeInRetailerName = retailers[storePricingInformationID]?.name
        var storeName = store?.name
        if (storeName == null) {
            storeName = storeInRetailerName
        }

        //if both are null then search entire hashmap for that store id
        if (storeName != null || retailer?.name != null) {
            return Pair(retailer?.name, storeName)
        }
        //if both are null then search entire hashmap for that store id
        var retailer_of_store = ""
        val nonUniqueStore = retailers.firstNotNullOf { (retailer, Retailer) ->
            retailer_of_store = retailer
            Retailer.stores?.firstOrNull { store ->
                store.id == storePricingInformationID
            }
        }

        val storeNameNonUnique = nonUniqueStore.name
        val retailerName = retailers[retailer_of_store]?.name

        return Pair(retailerName, storeNameNonUnique)
    }

    /**
     * Insert data in non-blocking fashion
     */
    fun insert(shoppingListRetailerProductInfo: RetailerProductInfo) = viewModelScope.launch {
        productRepository.insert(shoppingListRetailerProductInfo)
    }

    fun getProductFromID(productID: String): Flow<Product> {
        return flow {
            emit(productRepository.getProductFromID(productID))
        }
    }



}
