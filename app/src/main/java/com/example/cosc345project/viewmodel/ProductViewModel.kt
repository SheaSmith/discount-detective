package com.example.cosc345project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cosc345.shared.models.Product
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.RetailerProductInformation
import com.example.cosc345project.models.RetailerProductInfo
import com.example.cosc345project.repository.ProductRepository
import com.example.cosc345project.repository.RetailersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    retailersRepository: RetailersRepository,
    private val productRepository: ProductRepository
) : ViewModel() {
    val product = MutableStateFlow<Pair<String, Product>?>(null)
    val retailers = MutableStateFlow<Map<String, Retailer>>(mapOf())

    init {
        viewModelScope.launch {
            retailers.value = retailersRepository.getRetailers()
        }
    }

    val localRetailerInfo = MutableStateFlow(listOf<RetailerProductInformation>())
    val nonLocalRetailerInfo = MutableStateFlow(listOf<RetailerProductInformation>())

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

    fun addToShoppingList(
        productId: String,
        retailerProductInfoId: String,
        storeId: String,
        quantity: Int
    ) {
        viewModelScope.launch {
            productRepository.insert(
                RetailerProductInfo(
                    productId,
                    retailerProductInfoId,
                    storeId,
                    quantity
                )
            )
        }
    }
}