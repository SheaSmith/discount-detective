package com.example.cosc345project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cosc345.shared.models.Product
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.RetailerProductInformation
import com.example.cosc345project.models.ShoppingListItem
import com.example.cosc345project.repository.ProductRepository
import com.example.cosc345project.repository.RetailersRepository
import com.example.cosc345project.repository.ShoppingListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * The viewmodel for the product screen, which is responsible for connecting the UI and data layers.
 */
@HiltViewModel
class ProductViewModel @Inject constructor(
    retailersRepository: RetailersRepository,
    private val productRepository: ProductRepository,
    private val shoppingListRepository: ShoppingListRepository
) : ViewModel() {
    /**
     * The currently opened product.
     */
    val product: MutableStateFlow<Pair<String, Product>?> = MutableStateFlow(null)

    /**
     * The current list of retailers.
     */
    val retailers: MutableStateFlow<Map<String, Retailer>> = MutableStateFlow(mapOf())

    init {
        viewModelScope.launch {
            retailers.value = retailersRepository.getRetailers()
        }
    }

    /**
     * The information for local retailers for this product.
     */
    val localRetailerInfo: MutableStateFlow<List<RetailerProductInformation>> =
        MutableStateFlow(listOf())

    /**
     * The information for non-local retailers for this product.
     */
    val nonLocalRetailerInfo: MutableStateFlow<List<RetailerProductInformation>> =
        MutableStateFlow(listOf())

    /**
     * Get a product based on its ID.
     *
     * @param productId The ID of the product to get.
     */
    fun getProduct(productId: String) {
        viewModelScope.launch {
            val productInfo = productRepository.getProductFromID(productId)
            if (productInfo != null) {
                product.value = Pair(productId, productInfo)
                val localRetailers = retailers.value.filter { it.value.local == true }.keys

                if (localRetailers.isNotEmpty()) {
                    localRetailerInfo.value =
                        product.value!!.second.information!!.filter { localRetailers.contains(it.retailer) }
                    nonLocalRetailerInfo.value =
                        product.value!!.second.information!!.filter { !localRetailers.contains(it.retailer) }
                }
            }
            // TODO: do something here
        }
    }

    /**
     * Add this product to the shopping list.
     *
     * @param productId The ID of the product to add to the shopping list.
     * @param retailerProductInfoId The ID of the retailer product info the user has selected.
     * @param storeId The ID of the store whose pricing the user has selected.
     * @param quantity The quantity the user has selected.
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