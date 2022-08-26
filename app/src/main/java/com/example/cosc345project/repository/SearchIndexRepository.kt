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
 * The repository that handles indexing the product data from Firebase into our AppSearch index.
 *
 * @param context An instance of application context, required by AppSearch, and injected by Hilt.
 * @param database The Firebase Database reference we should use, again injected by Hilt.
 * @param retailersRepository The retailers repository, for getting information about the retailer.
 * @constructor This should not be used, instead inject the repository via Hilt.
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
     * Finish inserting products into AppSearch, so flush the database and close the session.
     */
    private suspend fun finish() {
        Log.d(TAG, "Flushing AppSearch database.")
        appSearchSession.requestFlushAsync().await()
        Log.d(TAG, "Manually closing AppSearch session. Dependent on session $dependentOnSession")
        if (!dependentOnSession)
            appSearchSession.close()
    }

    /**
     * Run the index from Firebase, including checking whether we need to do a sync at all.
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
     * Chunk the products (to save memory), pull them from Firebase and insert them into AppSearch.
     *
     * @param localMap A map of retailer IDs to whether they are considered local or not.
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
     * Get the first 10,000 products from Firebase, starting after the specified key.
     *
     * @param firstKey The key to start the query at, for paging.
     * @return A data snapshot from Firebase, containing the products.
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
     * Get the time of the last update, as a Unix timestamp, but in milliseconds.
     *
     * @return The Unix timestamp of the last update, except in milliseconds.
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
     * Map the data snapshot from Firebase into the correct product models we use.
     *
     * @param dataSnapshot The data snapshot to map.
     * @return A pair of the product ID, product map, and the last key in the list.
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
     * Insert the products into the AppSearch index.
     *
     * @param products The map of products to insert.
     * @param localMap A map with the retailer ID as the key, and the value being whether they are
     * considered local or not.
     * @return The result from AppSearch for whether this insert was successful.
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
     * Get a map with the retailer ID as the key and whether the retailer is local or not from
     * Firebase.
     *
     * @return The map.
     */
    private suspend fun getRetailersLocalMap(): Map<String, Boolean> {
        val localMap = retailersRepository.getRetailers().mapValues { it.value.local!! }
        Log.d(TAG, "Getting retailers local map. Retailer length ${localMap.size}.")
        return localMap
    }

    /**
     * Update the settings to specify whether the indexing has completed, and if so, update the last updated time.
     *
     * @param indexed Whether the indexing has completed or not.
     */
    private suspend fun setHasIndexed(indexed: Boolean) {
        context.indexSettingsDataStore.updateData {
            var builder = it.toBuilder()
                .setRunBefore(indexed)

            if (indexed) {
                builder = builder.setLastUpdated(System.currentTimeMillis())
            }

            builder.build()
        }
    }
}