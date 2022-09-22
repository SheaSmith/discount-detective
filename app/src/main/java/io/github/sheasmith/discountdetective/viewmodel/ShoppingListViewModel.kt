package io.github.sheasmith.discountdetective.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.RetailerProductInformation
import com.example.cosc345.shared.models.StorePricingInformation
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.sheasmith.discountdetective.models.ShoppingListItem
import io.github.sheasmith.discountdetective.repository.ProductRepository
import io.github.sheasmith.discountdetective.repository.RetailersRepository
import io.github.sheasmith.discountdetective.repository.ShoppingListRepository
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
    private val shoppingListRepository: ShoppingListRepository,
    private val retailersRepository: RetailersRepository
) : ViewModel() {

    private val allProducts: Flow<List<ShoppingListItem>> =
        shoppingListRepository.shoppingList

    /**
     * The current shopping list items
     */
    val shoppingList: MutableStateFlow<List<Triple<RetailerProductInformation, StorePricingInformation, ShoppingListItem>>?> =
        MutableStateFlow(null)

    /**
     * Update the state of the checkbox in the shoppingList
     * by copy then delete and insert so compose tracks changes
     */
    fun changeShoppingListChecked(item: ShoppingListItem, checked: Boolean) {
        viewModelScope.launch {
            item.checked = checked
            shoppingListRepository.updateChecked(item)
        }
    }

    /**
     * The current retailers from Firebase.
     */
    val retailers: MutableStateFlow<Map<String, Retailer>> = MutableStateFlow(mapOf())

    init {
        viewModelScope.launch {
            retailers.value = retailersRepository.getRetailers()
        }

        viewModelScope.launch {
            allProducts.collect { shoppingListItems ->
                shoppingList.value = shoppingListItems.mapNotNull { shoppingListInfo ->
                    val product = productRepository.getProductFromID(shoppingListInfo.productId)

                    if (product != null) {
                        val key =
                            product.information!!.first { info -> info.id == shoppingListInfo.retailerProductInformationId && info.pricing!!.any { it.store == shoppingListInfo.storeId } }
                        val value =
                            key.pricing!!.first { it.store == shoppingListInfo.storeId }

                        Triple(key, value, shoppingListInfo)
                    } else {
                        shoppingListRepository.deleteFromShoppingList(shoppingListInfo)
                        null
                    }
                }
            }
        }
    }

    /**
     * Get the name of the store based on the retailer and store IDs, including deciding whether to include the store name or not.
     *
     * @param retailerId The ID of the retailer to display.
     * @param storeId The ID of the store to display.
     * @param retailers All of the retailers to search from.
     * @return The store name to display.
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
     * Add a new item to the shopping list.
     *
     * @param shoppingListItem The shopping list item to add to the database.
     */
    fun insert(shoppingListItem: ShoppingListItem) {
        viewModelScope.launch {
            shoppingListRepository.addToShoppingList(shoppingListItem)
        }
    }

    fun delete(shoppingListItem: ShoppingListItem) {
        viewModelScope.launch {
            shoppingListRepository.deleteFromShoppingList(shoppingListItem)
        }
    }


}
