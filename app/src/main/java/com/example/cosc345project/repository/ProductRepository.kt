package com.example.cosc345project.repository

import androidx.annotation.WorkerThread
import com.example.cosc345project.dao.ProductDao
import com.example.cosc345project.database.ShoppingListDatabase
import com.example.cosc345project.models.ShoppingListRetailerProductInfo
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Hold the information related to the 'added' products
 * Basically, this holds the lists of products that belong to differnt
 * criteria.
 * Then the view models mutableStates are updated
 * Essentially the mutable states are like listeners but without all the extra code.
 *
 * So when someone 'adds' a product, is stored here as list.
 *
 * Needs to hold the retailer info, type checked etc
 *
 * use
 * Retailer product information
 * *Product Id, retailer Id
 */
@Singleton
class ProductRepository @Inject constructor(
    private val productDao: ProductDao
){
    //Room executes all Queries in separate thread
    //Observed flow will notify observer on change
    var allProducts: Flow<List<ShoppingListRetailerProductInfo>> = productDao.getProductIDs()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(shoppingListRetailerProductInfo: ShoppingListRetailerProductInfo) {
        productDao.insert(shoppingListRetailerProductInfo)
    }

}