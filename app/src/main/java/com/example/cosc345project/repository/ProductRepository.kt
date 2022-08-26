package com.example.cosc345project.repository

import android.content.ContentValues.TAG
import android.util.Log
import com.example.cosc345.shared.models.Product
import com.example.cosc345project.dao.ProductDao
import com.example.cosc345project.models.RetailerProductInfo
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Hold the information related to the 'added' products
 *
 * Basically, this holds the lists of products that belong to different criteria. Then the
 * viewmodel's mutableStates are updated.
 *
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
    private val productDao: ProductDao,
    private val database: FirebaseDatabase
) {
    /**
     * A flow containing all of the product IDs, updated whenever a new item is added.
     */
    var allProducts: Flow<List<RetailerProductInfo>> = productDao.getProductIDs()

    /**
     * Insert a product into the shopping list
     *
     * @param shoppingListRetailerProductInfo The shopping list item to add to the database.
     */
    suspend fun insert(shoppingListRetailerProductInfo: RetailerProductInfo) {
        productDao.insert(shoppingListRetailerProductInfo)
    }

    /**
     * Delete a product from the shopping list.
     *
     * @param shoppingListRetailerProductInfo The product to delete.
     */
    suspend fun delete(shoppingListRetailerProductInfo: RetailerProductInfo) {
        productDao.delete(shoppingListRetailerProductInfo)
    }

    /**
     * Get a product from Firebase corresponding to its given ID
     *
     * @param productID The product ID to query.
     * @return The product that matches this ID.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getProductFromID(productID: String): Product? {
        Log.d(TAG, "Get Firebase Product from ProductID.")
        return suspendCancellableCoroutine { continuation ->
            Log.d(TAG, "Get product from Firebase")
            database.reference.child("products").child(productID).get()
                .addOnSuccessListener { data ->
                    continuation.resume(data.getValue<Product>(), null)
                }
        }
    }
}