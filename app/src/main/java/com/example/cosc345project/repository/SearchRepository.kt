package com.example.cosc345project.repository

import android.content.Context
import android.util.Log
import androidx.appsearch.app.SearchResults
import androidx.appsearch.app.SearchSpec
import androidx.appsearch.app.SearchSpec.RANKING_STRATEGY_DOCUMENT_SCORE
import androidx.appsearch.app.SearchSpec.RANKING_STRATEGY_RELEVANCE_SCORE
import androidx.appsearch.app.SearchSuggestionSpec
import androidx.concurrent.futures.await
import com.example.cosc345.shared.extensions.capitaliseNZ
import com.example.cosc345.shared.extensions.titleCase
import com.example.cosc345.shared.models.Product
import com.example.cosc345project.checkInternet
import com.example.cosc345project.settings.indexSettingsDataStore
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: FirebaseDatabase
) : SearchBaseRepository(context) {
    companion object {
        private val TAG = SearchRepository::class.simpleName
    }

    suspend fun queryProductsAppSearch(query: String, count: Int): SearchResults {
        Log.d(TAG, "Query products from AppSearch index.")
        awaitInitialization()
        Log.d(TAG, "Initialised. Starting the AppSearch query.")

        val searchSpec = SearchSpec.Builder()
            .setRankingStrategy(if (query.isEmpty()) RANKING_STRATEGY_DOCUMENT_SCORE else RANKING_STRATEGY_RELEVANCE_SCORE)
            .setResultCountPerPage(count)
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
    ): Pair<Map<String, Product>, String?> {
        Log.d(TAG, "Query products from Firebase.")

        val result = getProductsFirebase(query, startAt, count)
        Log.d(TAG, "Queried Firebase, now pass results to pager.")

        return result
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getProductsFirebase(
        query: String,
        startAt: String?,
        count: Int
    ): Pair<Map<String, Product>, String?> {
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
                        products ?: mapOf(),
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
            checkInternet(context)
            firebaseQuery.get()
                .addOnSuccessListener(successListener)
        }
    }

    fun hasIndexedBefore(): Flow<Boolean> {
        return context.indexSettingsDataStore.data.map { it.runBefore }.distinctUntilChanged()
    }
}