package io.github.sheasmith.discountdetective.repository

import android.content.Context
import android.util.Log
import androidx.appsearch.app.AppSearchSession
import androidx.appsearch.app.SetSchemaRequest
import androidx.appsearch.localstorage.LocalStorage
import androidx.concurrent.futures.await
import io.github.sheasmith.discountdetective.models.SearchablePricingInformation
import io.github.sheasmith.discountdetective.models.SearchableProduct
import io.github.sheasmith.discountdetective.models.SearchableRetailerProductInformation
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first

/**
 * This repository contains common methods for AppSearch session management that are used by both
 * the [SearchRepository] and the [SearchIndexRepository].
 *
 * @param context The context to use to create the AppSearch session.
 */
abstract class SearchBaseRepository(
    private val context: Context,
    private val retailersRepository: RetailersRepository
) {
    companion object {
        private val TAG = SearchBaseRepository::class.simpleName
    }

    /**
     * A flow, specifying whether the AppSearch session for this repository has been started yet.
     */
    val isInitialized: MutableStateFlow<Boolean> = MutableStateFlow(false)

    /**
     * Whether something is dependent on the session existing (i.e. is it actively being used).
     */
    protected var dependentOnSession: Boolean = false

    /**
     * The app search session used.
     */
    protected lateinit var appSearchSession: AppSearchSession

    /**
     * Initialise the AppSearch session to allow for insertion or querying.
     *
     * @param waitForever Whether this should wait forever (and therefore be terminated whenever
     * the viewmodel is destroyed), or whether a manual call to close the session is required.
     */
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

    /**
     * Get a map with the retailer ID as the key and whether the retailer is local or not from
     * Firebase.
     *
     * @return The map.
     */
    suspend fun getRetailersMaps(): Pair<Map<String, Boolean>, Map<String, Map<String, String>>> {
        val retailers = retailersRepository.getRetailers()
        val storeRegionsMap =
            retailers.mapValues { retailer -> retailer.value.stores!!.associate { it.id!! to it.region!! } }
        val localMap = retailers.mapValues { it.value.local!! }
        Log.d(
            TAG,
            "Getting retailers local map. Retailer length ${localMap.size}."
        )
        return Pair(localMap, storeRegionsMap)
    }

    /**
     * A method that will wait until the AppSearch session is initialised.
     */
    suspend fun awaitInitialization() {
        if (!isInitialized.value) {
            isInitialized.first { it }
        }
    }
}