package com.example.cosc345project.viewmodel

import androidx.lifecycle.*
import com.example.cosc345.shared.models.Product
import com.example.cosc345.shared.models.RetailerProductInformation
import com.example.cosc345project.models.RetailerProductInfo
import com.example.cosc345project.repository.ProductRepository
import com.example.cosc345project.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
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
) : ViewModel() {

//    all products in the shopping list
    val allProducts: LiveData<List<RetailerProductInfo>> =
        productRepository.allProducts.asLiveData()

    /**
     * Insert data in non-blocking fashion
     */
    fun insert(shoppingListRetailerProductInfo :RetailerProductInfo) = viewModelScope.launch {
        productRepository.insert(shoppingListRetailerProductInfo)
    }

    fun getProductFromID(productID: String): Flow<Product>{
        return flow {
            emit(productRepository.getProductFromID(productID))
        }
    }



}
