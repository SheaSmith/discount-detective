package com.example.cosc345.scraperapp.repositories

import com.example.cosc345.scraper.Matcher
import com.example.cosc345.shared.models.Product
import com.example.cosc345.shared.models.Retailer
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ScraperRepository class
 *
 * @param firebaseDatabase Our database - containing retailers, products, etc.
 * @param temporaryDatabaseRepository
 * @param matcher Matches products across retailers/stores
 */
@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class ScraperRepository @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val temporaryDatabaseRepository: TemporaryDatabaseRepository,
    private val matcher: Matcher
) {
    /**
     * Run all of the scrapers, and save the results.
     */
    suspend fun runScrapers() {
        val result = matcher.runScrapers()
        temporaryDatabaseRepository.clearDatabase()
        temporaryDatabaseRepository.insertRetailerProductInfo(result.second)
        temporaryDatabaseRepository.insertRetailers(result.first)
    }

    /**
     * Group retailer product info from the database by barcode and then save back to the database.
     */
    suspend fun matchBarcodes() {
        val retailers = temporaryDatabaseRepository.getRetailers()
        val productInfo = temporaryDatabaseRepository.getRetailerProductInfo()
        val result = matcher.matchBarcodes(productInfo, retailers)
        temporaryDatabaseRepository.clearDatabase()
        temporaryDatabaseRepository.insertRetailers(result.first)
        temporaryDatabaseRepository.insertProducts(result.second)
    }

    /**
     * Group all retailer product information from the database by the name values, and save it back to the database.
     */
    suspend fun matchValues() {
        val retailers = temporaryDatabaseRepository.getRetailers()
        val products = temporaryDatabaseRepository.getProducts()
        val result = matcher.matchNames(products, retailers)
        temporaryDatabaseRepository.clearDatabase()
        temporaryDatabaseRepository.insertRetailers(result.first)
        temporaryDatabaseRepository.insertProducts(result.second)
    }

    /**
     * Upload the contents of the database to Firebase.
     */
    suspend fun saveToFirebase() {
        val retailers = temporaryDatabaseRepository.getRetailers()
        val products = temporaryDatabaseRepository.getProducts()
        saveScrapers(retailers, products)
    }

    private suspend fun saveScrapers(
        retailers: Map<String, Retailer>,
        products: Map<String, Product>
    ) {
        saveRetailers(retailers)

        val deletedProducts = getKeys()
        deletedProducts.removeAll(products.keys)

        deleteProducts(deletedProducts)
        updateDeletedProductsList(deletedProducts)

        products.forEach {
            saveProduct(it.key, it.value)
        }

        setLastUpdated()
    }

    private suspend fun deleteProducts(keys: List<String>) {
        return suspendCancellableCoroutine { continuation ->
            firebaseDatabase.getReference("products").updateChildren(keys.associateWith { null })
                .addOnSuccessListener {
                    continuation.resume(run {}, null)
                }
        }
    }

    private suspend fun updateDeletedProductsList(keys: List<String>) {
        return suspendCancellableCoroutine { continuation ->
            firebaseDatabase.getReference("deletedProducts").setValue(keys).addOnSuccessListener {
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

    private suspend fun getKeys(): MutableList<String> {
        var lastKey: String? = null
        var lastSize = 5000
        val keys = mutableListOf<String>()

        while (lastSize == 5000) {
            val result = getSomeKeys(lastKey)
            keys.addAll(result)
            lastKey = result.last()
            lastSize = result.size
        }

        return keys
    }

    private suspend fun getSomeKeys(start: String? = null): List<String> {
        var query = firebaseDatabase.reference.child("products")
            .orderByKey()
            .limitToFirst(5000)

        if (start != null) {
            query = query.startAt(start)
        }

        return suspendCancellableCoroutine { continuation ->
            query.get().addOnSuccessListener { snapshot ->
                continuation.resume(snapshot.children.map { it.key!! }, null)
            }
        }
    }

    private suspend fun saveProduct(key: String, product: Product) {
        return suspendCancellableCoroutine { continuation ->
            firebaseDatabase.getReference("products").child(key).setValue(product)
                .addOnSuccessListener {
                    System.gc()
                    continuation.resume(run {}, null)
                }
        }
    }

    private suspend fun setLastUpdated() {
        return suspendCancellableCoroutine { continuation ->
            firebaseDatabase.getReference("lastUpdated").setValue(System.currentTimeMillis())
                .addOnSuccessListener {
                    continuation.resume(run {}, null)
                }
        }
    }
}