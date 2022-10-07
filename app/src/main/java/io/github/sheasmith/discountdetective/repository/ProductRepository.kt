package io.github.sheasmith.discountdetective.repository

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.example.cosc345.shared.models.Product
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.sheasmith.discountdetective.checkInternet
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A repository for getting information about a product.
 */
@Singleton
class ProductRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: FirebaseDatabase
) {
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

        checkInternet(context)
    }
}