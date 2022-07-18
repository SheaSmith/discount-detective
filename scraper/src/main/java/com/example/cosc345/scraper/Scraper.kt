package com.example.cosc345.scraper

import com.example.cosc345.scraper.model.ScraperResult
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * The interface which defines the basic scraper framework.
 */
abstract class Scraper {
    /**
     * Run the scraper.
     *
     * @return The result of the scraper, containing retailer, store and product information.
     */
    abstract fun runScraper(): ScraperResult

    protected fun <T> generateRequest(cls: Class<T>): T {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

        return retrofit.create(cls)
    }
}