package com.example.cosc345.scraperapp

import com.example.cosc345.scraper.Matcher
import com.example.cosc345.shared.models.Product
import com.example.cosc345.shared.models.Retailer
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class ScraperRepository @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase
) {
    suspend fun runScrapers(): Pair<Map<String, Retailer>, Map<String, Product>> {
        return Matcher().run()
    }

    suspend fun saveScrapers(retailers: Map<String, Retailer>, products: Map<String, Product>) {
        saveRetailers(retailers)

        deleteProducts()

        products.forEach {
            saveProduct(it.key, it.value)
        }
    }


    private suspend fun deleteProducts() {
        return suspendCancellableCoroutine { continuation ->
            firebaseDatabase.getReference("products").removeValue().addOnSuccessListener {
                continuation.resume(run {}, null)
            }
        }
    }

    private suspend fun saveRetailers(retailers: Map<String, Retailer>) {
        return suspendCancellableCoroutine { continuation ->
            firebaseDatabase.getReference("retailers").setValue(retailers).addOnSuccessListener {
                continuation.resume(run {}, null)
            }
        }
    }

    private suspend fun saveProduct(key: String, product: Product) {
        return suspendCancellableCoroutine { continuation ->
            firebaseDatabase.getReference("products").child(key).setValue(product)
                .addOnSuccessListener {
                    continuation.resume(run {}, null)
                }
        }
    }
}