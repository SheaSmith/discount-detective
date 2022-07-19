package com.example.cosc345.scraper.interfaces

import com.example.cosc345.scraper.models.ScraperResult
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * The interface which defines the basic scraper framework.
 */
abstract class Scraper() {
    /**
     * Run the scraper.
     *
     * @return The result of the scraper, containing retailer, store and product information.
     */
    abstract suspend fun runScraper(): ScraperResult

    protected fun <T> generateRequest(cls: Class<T>, baseUrl: String): T {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create())
            // Dummy base URL as it is required by Retrofit
            .baseUrl(baseUrl)
            .build()

        return retrofit.create(cls)
    }
}