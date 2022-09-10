package io.github.sheasmith.discountdetective.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.RetailerProductInformation
import com.example.cosc345.shared.models.StorePricingInformation
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.sheasmith.discountdetective.models.RetailerProductInfo
import io.github.sheasmith.discountdetective.repository.ProductRepository
import io.github.sheasmith.discountdetective.repository.RetailersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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

    private val allProducts: Flow<List<RetailerProductInfo>> =
        productRepository.allProducts

    val products: MutableStateFlow<List<Triple<RetailerProductInformation, StorePricingInformation, Int>>?> =
        MutableStateFlow(null)

    val retailers = MutableStateFlow<Map<String, Retailer>>(mapOf())

    init {
        viewModelScope.launch {
            retailers.value = retailersRepository.getRetailers()
        }

        viewModelScope.launch {
            allProducts.collect { shoppingListItems ->
                products.value = shoppingListItems.mapNotNull { shoppingListInfo ->
                    val product = productRepository.getProductFromID(shoppingListInfo.productID)

                    if (product != null) {
                        val key =
                            product.information!!.first { info -> info.id == shoppingListInfo.retailerProductInformationID && info.pricing!!.any { it.store == shoppingListInfo.storePricingInformationID } }
                        val value =
                            key.pricing!!.first { it.store == shoppingListInfo.storePricingInformationID }

                        Triple(key, value, shoppingListInfo.quantity)
                    } else {
                        productRepository.delete(shoppingListInfo)

                        null
                    }
                }
            }
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
        retailerId: String,
        storeId: String,
        retailers: Map<String, Retailer>
    ): String? {
        val retailer = retailers[retailerId]

        val store = retailer?.stores?.firstOrNull { it.id == storeId }

        if (store?.name == retailer?.name) {
            return retailer?.name
        }

        if (retailer != null && store != null)
            return "${retailer.name} ${store.name}"

        return null
    }

    /**
     * Insert data in non-blocking fashion
     */
    fun insert(shoppingListRetailerProductInfo: RetailerProductInfo) = viewModelScope.launch {
        productRepository.insert(shoppingListRetailerProductInfo)
    }


}
