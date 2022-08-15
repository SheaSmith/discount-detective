package com.example.cosc345project.viewmodel

import androidx.lifecycle.*
import com.example.cosc345.shared.models.Product
import com.example.cosc345.shared.models.RetailerProductInformation
import com.example.cosc345project.models.ShoppingListRetailerProductInfo
import com.example.cosc345project.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val productRepository: ProductRepository
) : ViewModel() {

    private val allProducts: LiveData<List<ShoppingListRetailerProductInfo>> =
        productRepository.allProducts.asLiveData()

    /**
     * Insert data in non-blocking fashion
     */
    fun insert(shoppingListRetailerProductInfo :ShoppingListRetailerProductInfo) = viewModelScope.launch {
        productRepository.insert(shoppingListRetailerProductInfo)
    }

}
