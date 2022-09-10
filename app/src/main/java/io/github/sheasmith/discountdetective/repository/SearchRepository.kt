package io.github.sheasmith.discountdetective.repository

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
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.sheasmith.discountdetective.checkInternet
import io.github.sheasmith.discountdetective.settings.indexSettingsDataStore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The search repository which handles querying AppSearch (or Firebase, depending if AppSearch is
 * available).
 *
 * @param context The application context for AppSearch, supplied by Hilt.
 * @param database The Firebase database to query off of, if necessary.
 * @constructor Should not be used, instead inject this repository using Hilt.
 */
@Singleton
class SearchRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: FirebaseDatabase
) : SearchBaseRepository(context) {
    companion object {
        private val TAG = SearchRepository::class.simpleName
    }

    /**
     * Query products from AppSearch based on the specified query.
     *
     * @param query The query to search based on.
     * @param count The number of results to return.
     * @return AppSearch search results, including a way to page and retrieve data.
     */
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

    /**
     * Get search suggestions from AppSearch, based on the current query.
     *
     * @param query The query to get suggestions for.
     * @return A list of suggestions.
     */
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

    /**
     * Get products from Firebase with the specified query.
     *
     * @param query The query to use to search Firebase. This is only done based on the first information's name.
     * @param startAt The key to start at for paging.
     * @param count The number of results to return.
     * @return A pair of the map of product IDs to products, and the ID of the last item.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun queryProductsFirebase(
        query: String,
        startAt: String?,
        count: Int
    ): Pair<Map<String, Product>, String?> {
        Log.d(TAG, "Get products from Firebase.")

        return suspendCancellableCoroutine { continuation ->
            val successListener = OnSuccessListener<DataSnapshot> { snapshot ->
                Log.d(TAG, "Successfully retrieved products from Firebase.")
                val products = snapshot.getValue<HashMap<String, Product>>()

                val keys = snapshot.children.map { it.key }

                val key = if (query.isEmpty()) {
                    keys.first()
                } else {
                    products?.get(keys.last())?.information?.first()?.name
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
                    .endAt("${query.titleCase().capitaliseNZ()}~")
                    .limitToFirst(count)
            }

            if (query.isNotEmpty() && startAt != null) {
                firebaseQuery = firebaseQuery.startAfter(startAt)
            } else if (query.isNotEmpty()) {
                firebaseQuery = firebaseQuery.startAt(query.titleCase().capitaliseNZ())
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

    /**
     * Get whether the index has been run or not, so we can display a warning if it hasn't yet.
     */
    fun hasIndexedBefore(): Flow<Boolean> {
        return context.indexSettingsDataStore.data.map { it.runBefore }.distinctUntilChanged()
    }
}