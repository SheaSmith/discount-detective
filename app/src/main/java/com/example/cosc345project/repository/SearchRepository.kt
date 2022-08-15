package com.example.cosc345project.repository

import android.content.Context
import android.util.Log
import androidx.appsearch.app.*
import androidx.appsearch.app.SearchSpec.RANKING_STRATEGY_RELEVANCE_SCORE
import androidx.appsearch.localstorage.LocalStorage
import androidx.concurrent.futures.await
import com.example.cosc345.shared.extensions.capitaliseNZ
import com.example.cosc345.shared.extensions.titleCase
import com.example.cosc345.shared.models.Product
import com.example.cosc345project.models.SearchablePricingInformation
import com.example.cosc345project.models.SearchableProduct
import com.example.cosc345project.models.SearchableRetailerProductInformation
import com.example.cosc345project.settings.indexSettingsDataStore
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: FirebaseDatabase,
    private val retailersRepository: RetailersRepository
) {
    companion object {
        private val TAG = SearchRepository::class.simpleName
    }

    val isInitialized: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private var dependentOnSession: Boolean = false
    private lateinit var appSearchSession: AppSearchSession

    suspend fun initialise(waitForever: Boolean = true) {
        Log.d(TAG, "Initialising AppSearch index.")
        Log.d(TAG, "Creating search session.")
        appSearchSession =
            LocalStorage.createSearchSessionAsync(
                LocalStorage.SearchContext.Builder(
                    context,
                    "products"
                )
                    .build()
            ).await()

        try {
            Log.d(TAG, "Checking if AppSearch schema should be set.")
            if (appSearchSession.schemaAsync.await().schemas.isEmpty()) {
                Log.d(TAG, "Setting AppSearch schema.")
                val setSchemaRequest = SetSchemaRequest.Builder()
                    .addDocumentClasses(
                        SearchableProduct::class.java,
                        SearchableRetailerProductInformation::class.java,
                        SearchablePricingInformation::class.java
                    )
                    .setForceOverride(true)
                    .build()

                appSearchSession.setSchemaAsync(setSchemaRequest).await()
            }

            Log.d(TAG, "Initialised AppSearch, so allow other dependent functions to start.")
            isInitialized.value = true

            if (waitForever) {
                dependentOnSession = true
                Log.d(TAG, "Awaiting AppSearch cancellation.")
                awaitCancellation()
            }
        } finally {
            if (waitForever) {
                Log.d(TAG, "Saving AppSearch database.")
                appSearchSession.requestFlushAsync().await()
                Log.d(TAG, "Closing AppSearch session.")
                appSearchSession.close()
            }
        }
    }

    private suspend fun finish() {
        Log.d(TAG, "Flushing AppSearch database.")
        appSearchSession.requestFlushAsync().await()
        Log.d(TAG, "Manually closing AppSearch session. Dependent on session $dependentOnSession")
        if (!dependentOnSession)
            appSearchSession.close()
    }

    private suspend fun getRetailersLocalMap(): Map<String, Boolean> {
        val localMap = retailersRepository.getRetailers().mapValues { it.value.local!! }
        Log.d(TAG, "Getting retailers local map. Retailer length ${localMap.size}.")
        return localMap
    }

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

    suspend fun hasIndexedBefore(): Boolean {
        return context.indexSettingsDataStore.data.first().runBefore
    }

    suspend fun queryProductsAppSearch(query: String, count: Int): SearchResults {
        Log.d(TAG, "Query products from AppSearch index.")
        awaitInitialization()
        Log.d(TAG, "Initialised. Starting the AppSearch query.")

        val searchSpec = SearchSpec.Builder()
            .setRankingStrategy(RANKING_STRATEGY_RELEVANCE_SCORE)
            .setSnippetCount(count)
            .build()

        Log.d(TAG, "Running query for AppSearch.")

        val searchResults = appSearchSession.search(query, searchSpec)

        Log.d(TAG, "Finished query for AppSearch, now pass to pager.")
        return searchResults
    }

    suspend fun searchSuggestions(query: String): List<String> {
        Log.d(TAG, "Query search suggestions from AppSearch.")
        awaitInitialization()
        Log.d(TAG, "Initialised. Starting the search suggestions query.")

        val suggestionsSpec = SearchSuggestionSpec.Builder(5)
            .build()

        Log.d(TAG, "Running query for suggestions.")

        return if (query.isNotEmpty()) {
            val suggestions = appSearchSession.searchSuggestionAsync(query, suggestionsSpec).await()
            Log.d(TAG, "Finished suggestions query.")
            suggestions.map { it.suggestedResult }
        } else {
            listOf()
        }
    }

    suspend fun queryProductsFirebase(
        query: String,
        startAt: String?,
        count: Int
    ): Pair<List<SearchableProduct>, String?> {
        Log.d(TAG, "Query products from Firebase.")

        val localMap = getRetailersLocalMap()
        Log.d(TAG, "Got local map of retailers for Firebase.")

        val result = getProductsFirebase(localMap, query, startAt, count)
        Log.d(TAG, "Queried Firebase, now pass results to pager.")

        return result
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getProductsFirebase(
        localMap: Map<String, Boolean>,
        query: String,
        startAt: String?,
        count: Int
    ): Pair<List<SearchableProduct>, String?> {
        Log.d(TAG, "Get products from Firebase.")

        return suspendCancellableCoroutine { continuation ->
            val successListener = OnSuccessListener<DataSnapshot> { snapshot ->
                Log.d(TAG, "Successfully retrieved products from Firebase.")
                val products = snapshot.getValue<Map<String, Product>>()

                val key = if (query.isEmpty()) {
                    products?.keys?.first()
                } else {
                    products?.values?.last()?.information?.first()?.name
                }


                continuation.resume(
                    Pair(
                        products?.map { SearchableProduct(it.value, it.key, localMap) } ?: listOf(),
                        key
                    ),
                    null
                )
            }

            Log.d(TAG, "Building Firebase query.")
            var firebaseQuery = if (query.isEmpty()) {
                database.reference
                    .child("products")
                    .orderByKey()
                    .limitToLast(count)
            } else {
                database.reference
                    .child("products")
                    .orderByChild("information/0/name")
                    .startAt(startAt ?: query.titleCase().capitaliseNZ())
                    .endAt("${query.titleCase().capitaliseNZ()}~")
                    .limitToFirst(count)
            }

            if (query.isEmpty() && startAt != null) {
                firebaseQuery = firebaseQuery.endBefore(startAt)
            }

            Log.d(TAG, "Query Firebase.")
            firebaseQuery.get()
                .addOnSuccessListener(successListener)
        }
    }

    private suspend fun awaitInitialization() {
        if (!isInitialized.value) {
            isInitialized.first { it }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getLastUpdate(): Long {
        Log.d(TAG, "Get Firebase last update.")
        return suspendCancellableCoroutine { continuation ->
            Log.d(TAG, "Get value from Firebase.")
            database.reference.child("lastUpdated").get().addOnSuccessListener { data ->
                Log.d(TAG, "Got last updated from Firebase, return value.")
                continuation.resume(data.getValue<Long>() ?: 0, null)
            }
        }
    }

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
            initialise(false)

            if (!hasIndexedBefore()) {
                appSearchSession.setSchemaAsync(
                    SetSchemaRequest.Builder().setForceOverride(true).build()
                )

                initialise(false)
            }

            Log.d(TAG, "Initialised. Get retailers local map from Firebase.")
            val retailers = getRetailersLocalMap()

            Log.d(TAG, "Insert products from Firebase into index.")
            insertProducts(retailers)

            Log.d(TAG, "Finished insertion, close search session.")
            finish()

            Log.d(TAG, "Finished closing AppSearch session.")

            Log.d(TAG, "Save last updated time to settings.")
            context.indexSettingsDataStore.updateData {
                it.toBuilder()
                    .setLastUpdated(System.currentTimeMillis())
                    .setRunBefore(true)
                    .build()
            }
        }
    }

    private fun mapFirebaseDataSnapshot(dataSnapshot: DataSnapshot): Pair<List<Pair<String, Product>>, String> {
        Log.d(TAG, "Got products from Firebase, map them appropriately.")
        val products: List<Pair<String, Product>> =
            dataSnapshot.children.map { Pair(it.key!!, it.getValue<Product>()!!) }

        Log.d(TAG, "Get key for next request.")
        val newKey = products.last().first

        return Pair(products, newKey)
    }

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
            query.get().addOnSuccessListener { dataSnapshot ->
                Log.d(TAG, "Return results.")
                continuation.resume(dataSnapshot, null)
            }
        }
    }

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
}