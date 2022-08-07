package com.example.cosc345project.repository

import android.content.Context
import android.util.Log
import androidx.appsearch.app.*
import androidx.appsearch.app.SearchSpec.RANKING_STRATEGY_DOCUMENT_SCORE
import androidx.appsearch.localstorage.LocalStorage
import androidx.work.await
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
    private val isInitialized: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private lateinit var appSearchSession: AppSearchSession

    suspend fun initialise(waitForever: Boolean = true) {
        appSearchSession =
            LocalStorage.createSearchSessionAsync(
                LocalStorage.SearchContext.Builder(
                    context,
                    "products"
                )
                    .build()
            ).await()

        try {
            val setSchemaRequest = SetSchemaRequest.Builder()
                .addDocumentClasses(
                    SearchableProduct::class.java,
                    SearchableRetailerProductInformation::class.java,
                    SearchablePricingInformation::class.java
                )
                .build()

            appSearchSession.setSchemaAsync(setSchemaRequest).await()

            isInitialized.value = true

            if (waitForever)
                awaitCancellation()
        } finally {
            if (waitForever)
                appSearchSession.close()
        }
    }

    private suspend fun finish() {
        appSearchSession.requestFlushAsync().await()
        appSearchSession.close()
    }

    suspend fun getRetailers(): Map<String, Boolean> {
        return retailersRepository.getRetailers().mapValues { it.value.local!! }
    }

    private suspend fun setProducts(
        products: List<Pair<String, Product>>,
        localMap: Map<String, Boolean>
    ): AppSearchBatchResult<String, Void> {
        awaitInitialization()

        val request = PutDocumentsRequest.Builder()
            .addDocuments(products.map { SearchableProduct(it.second, it.first, localMap) })
            .build()

        val test = appSearchSession.putAsync(request).await()
        Log.d("Yes", "Tes")
        return test
    }

    suspend fun queryProducts(query: String, count: Int): SearchResults {
        awaitInitialization()

        val searchSpec = SearchSpec.Builder()
            .setRankingStrategy(RANKING_STRATEGY_DOCUMENT_SCORE)
            .setResultCountPerPage(count)
            .build()

        val searchResults = appSearchSession.search(query, searchSpec)
        Log.d("Searc", "results")
        return searchResults
    }

    suspend fun queryProductsFirebase(
        query: String,
        startAt: String?,
        count: Int
    ): Pair<List<SearchableProduct>, String> {
        val localMap = getRetailers()
        return getProductsFirebase(localMap, query, startAt, count)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getProductsFirebase(
        localMap: Map<String, Boolean>,
        query: String,
        startAt: String?,
        count: Int
    ): Pair<List<SearchableProduct>, String> {
        return suspendCancellableCoroutine { continuation ->
            val successListener = OnSuccessListener<DataSnapshot> { snapshot ->
                val key = snapshot.children.last().key!!

                continuation.resume(
                    Pair(snapshot
                        .getValue<Map<String, Product>>()
                        ?.map { SearchableProduct(it.value, it.key, localMap) }
                        ?: listOf(), key),
                    null
                )
            }

            var firebaseQuery = database.reference
                .child("products")
                .orderByChild("information/0/name")
                .startAt(query.titleCase().capitaliseNZ())
                .endAt("${query.titleCase().capitaliseNZ()}~")
                .limitToFirst(count)

            if (startAt != null) {
                firebaseQuery = firebaseQuery.startAfter(startAt)
            }

            firebaseQuery.get()
                .addOnSuccessListener(successListener)
        }
    }

    private suspend fun awaitInitialization() {
        if (!isInitialized.value) {
            isInitialized.first { it }
        }
    }

    suspend fun isAny(): Boolean {
        awaitInitialization()

        val searchSpec = SearchSpec.Builder()
            .setSnippetCount(1)
            .build()

        val searchResults = appSearchSession.search("", searchSpec).nextPageAsync.await()

        val result = searchResults.firstOrNull()
        print("Test")
        return result != null
    }

    suspend fun hasIndexed(): Boolean {
        return context.indexSettingsDataStore.data.first().runBefore
    }

    suspend fun saveSearchDatabase() {
        appSearchSession.requestFlushAsync().await()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getLastUpdate(): Long {
        return suspendCancellableCoroutine { continuation ->
            database.reference.child("lastUpdated").get().addOnSuccessListener { data ->
                continuation.resume(data.getValue<Long>() ?: 0, null)
            }
        }
    }

    suspend fun indexFromFirebase() {
        val lastUpdate = context.indexSettingsDataStore.data.first().lastUpdated
        val databaseLastUpdated = getLastUpdate()

        if (lastUpdate <= databaseLastUpdated) {
            initialise(false)

            val retailers = getRetailers()

            insertProducts(retailers)

            finish()

            context.indexSettingsDataStore.updateData {
                it.toBuilder()
                    .setLastUpdated(databaseLastUpdated + 1)
                    .setRunBefore(true)
                    .build()
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun getProducts(firstKey: String? = null): Pair<List<Pair<String, Product>>, String> {
        return suspendCancellableCoroutine { continuation ->
            var query = database.reference
                .child("products")
                .limitToFirst(10000)
                .orderByKey()

            if (firstKey != null) {
                query = query.startAt(firstKey)
            }

            query.get().addOnSuccessListener {
                val products: List<Pair<String, Product>>? =
                    it.children.map { Pair(it.key!!, it.getValue<Product>()!!) }

                val newKey = products!!.last().first

                continuation.resume(Pair(products, newKey), null)
            }
        }
    }

    @Suppress("UNUSED_VALUE")
    private suspend fun insertProducts(
        localMap: Map<String, Boolean>
    ) {
        var lastSize = 10000
        var firstKey: String? = null

        while (lastSize == 10000) {
            val result = getProducts(firstKey)
            firstKey = result.second
            setProducts(result.first, localMap)
            lastSize = result.first.size
        }
    }
}