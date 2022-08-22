package com.example.cosc345project.repository

import android.content.Context
import android.util.Log
import androidx.appsearch.app.AppSearchBatchResult
import androidx.appsearch.app.PutDocumentsRequest
import androidx.appsearch.app.SetSchemaRequest
import androidx.concurrent.futures.await
import com.example.cosc345.shared.models.Product
import com.example.cosc345project.checkInternet
import com.example.cosc345project.models.SearchableProduct
import com.example.cosc345project.settings.indexSettingsDataStore
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Search Index Repository class.
 *
 * ???
 *
 * @param context
 * @param database
 * @param retailersRepository
 */
@Singleton
class SearchIndexRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: FirebaseDatabase,
    private val retailersRepository: RetailersRepository
) : SearchBaseRepository(context) {
    companion object {
        private val TAG = SearchIndexRepository::class.simpleName
    }

    /**
     * Finish function
     *
     * ???
     */
    private suspend fun finish() {
        Log.d(TAG, "Flushing AppSearch database.")
        appSearchSession.requestFlushAsync().await()
        Log.d(TAG, "Manually closing AppSearch session. Dependent on session $dependentOnSession")
        if (!dependentOnSession)
            appSearchSession.close()
    }

    /**
     * Index From Firebase
     *
     * ???
     */
    suspend fun indexFromFirebase() {
        Log.d(
            TAG,
            "Potentially index AppSearch from Firebase. Get the last updated from the datastore."
        )
        val lastUpdate = context.indexSettingsDataStore.data.first().lastUpdated
        Log.d(TAG, "Get the last updated from Firebase.")
        val databaseLastUpdated = getLastUpdate()

        Log.d(TAG, "Check whether we should update.")
        if (lastUpdate <= databaseLastUpdated) {
            Log.d(TAG, "We should update, initialise AppSearch.")
            setHasIndexed(false)

            initialise(false)

            appSearchSession.setSchemaAsync(
                SetSchemaRequest.Builder().setForceOverride(true).build()
            )

            initialise(false)

            Log.d(TAG, "Initialised. Get retailers local map from Firebase.")
            val retailers = getRetailersLocalMap()

            Log.d(TAG, "Insert products from Firebase into index.")
            insertProducts(retailers)

            Log.d(TAG, "Finished insertion, close search session.")
            finish()

            Log.d(TAG, "Finished closing AppSearch session.")

            Log.d(TAG, "Save last updated time to settings.")

        }

        setHasIndexed(true)
    }

    /**
     * Insert Products function.
     *
     * ???
     */
    @Suppress("UNUSED_VALUE")
    private suspend fun insertProducts(
        localMap: Map<String, Boolean>
    ) {
        Log.d(TAG, "Insert products from Firebase into AppSearch.")
        var lastSize = 10000
        var firstKey: String? = null

        while (lastSize == 10000) {
            Log.d(TAG, "Get products from Firebase.")
            val dataSnapshot = getProducts(firstKey)
            Log.d(TAG, "Map Data Snapshot from Firebase.")
            val result = mapFirebaseDataSnapshot(dataSnapshot)
            Log.d(TAG, "Mapped Data Snapshot.")
            firstKey = result.second
            Log.d(TAG, "Insert this set of products from Firebase into the AppSearch index.")
            setProducts(result.first, localMap)
            lastSize = result.first.size
        }

        Log.d(TAG, "Finished putting products in search index.")
    }

    /**
     * Get Products function.
     *
     * ???
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun getProducts(firstKey: String? = null): DataSnapshot {
        Log.d(TAG, "Get all products from Firebase, from a specific key.")
        return suspendCancellableCoroutine { continuation ->
            Log.d(TAG, "Build Firebase query for all products.")
            var query = database.reference
                .child("products")
                .limitToFirst(10000)
                .orderByKey()

            if (firstKey != null) {
                query = query.startAt(firstKey)
            }

            Log.d(TAG, "Run query for all products.")
            checkInternet(context)
            query.get().addOnSuccessListener { dataSnapshot ->
                Log.d(TAG, "Return results.")
                continuation.resume(dataSnapshot, null)
            }
        }
    }

    /**
     * Get Last Update function
     *
     * ???
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getLastUpdate(): Long {
        Log.d(TAG, "Get Firebase last update.")
        return suspendCancellableCoroutine { continuation ->
            Log.d(TAG, "Get value from Firebase.")
            checkInternet(context)
            database.reference.child("lastUpdated").get().addOnSuccessListener { data ->
                Log.d(TAG, "Got last updated from Firebase, return value.")
                continuation.resume(data.getValue<Long>() ?: 0, null)
            }
        }
    }

    /**
     * Map Firebase Data Snapshot
     *
     * @param dataSnapshot
     */
    private fun mapFirebaseDataSnapshot(dataSnapshot: DataSnapshot): Pair<List<Pair<String, Product>>, String> {
        Log.d(TAG, "Got products from Firebase, map them appropriately.")
        val products: List<Pair<String, Product>> =
            dataSnapshot.children.map { Pair(it.key!!, it.getValue<Product>()!!) }

        Log.d(TAG, "Get key for next request.")
        val newKey = products.last().first

        return Pair(products, newKey)
    }

    /**
     * Set Products
     *
     * @param products
     * @param localMap
     */
    private suspend fun setProducts(
        products: List<Pair<String, Product>>,
        localMap: Map<String, Boolean>
    ): AppSearchBatchResult<String, Void> {
        Log.d(TAG, "Adding products to AppSearch database. Awaiting initialisation.")
        awaitInitialization()

        Log.d(TAG, "Initialised. Start put documents request for AppSearch.")

        val request = PutDocumentsRequest.Builder()
            .addDocuments(products.map { SearchableProduct(it.second, it.first, localMap) })
            .build()

        Log.d(TAG, "Put documents into AppSearch index.")

        val result = appSearchSession.putAsync(request).await()

        Log.d(TAG, "Finished putting documents into the AppSearch index.")
        return result
    }

    /**
     * getRetailersLocalMap function
     *
     * ???
     */
    private suspend fun getRetailersLocalMap(): Map<String, Boolean> {
        val localMap = retailersRepository.getRetailers().mapValues { it.value.local!! }
        Log.d(TAG, "Getting retailers local map. Retailer length ${localMap.size}.")
        return localMap
    }

    /**
     * setHasIndexed function
     *
     * ???
     *
     * @param indexed
     */
    private suspend fun setHasIndexed(indexed: Boolean) {
        context.indexSettingsDataStore.updateData {
            it.toBuilder()
                .setLastUpdated(System.currentTimeMillis())
                .setRunBefore(indexed)
                .build()
        }
    }
}