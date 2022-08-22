package com.example.cosc345project.repository

import android.content.Context
import android.util.Log
import androidx.appsearch.app.AppSearchSession
import androidx.appsearch.app.SetSchemaRequest
import androidx.appsearch.localstorage.LocalStorage
import androidx.concurrent.futures.await
import com.example.cosc345project.models.SearchablePricingInformation
import com.example.cosc345project.models.SearchableProduct
import com.example.cosc345project.models.SearchableRetailerProductInformation
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first

/**
 * Search Base Repo abstract class.
 *
 * Controls searching sessions, enabling users to search for products within the firebase database.
 *
 * @param context
 */
abstract class SearchBaseRepository(private val context: Context) {
    companion object {
        private val TAG = SearchBaseRepository::class.simpleName
    }

    val isInitialized: MutableStateFlow<Boolean> = MutableStateFlow(false)
    protected var dependentOnSession: Boolean = false

    protected lateinit var appSearchSession: AppSearchSession

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

    protected suspend fun awaitInitialization() {
        if (!isInitialized.value) {
            isInitialized.first { it }
        }
    }
}