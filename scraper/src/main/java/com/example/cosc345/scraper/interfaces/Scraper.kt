package com.example.cosc345.scraper.interfaces

import com.example.cosc345.scraper.helpers.getMoshi
import com.example.cosc345.scraper.interceptors.RetrofitRetryInterceptor
import com.example.cosc345.scraper.models.ScraperResult
import com.example.cosc345.shared.models.Units
import okhttp3.OkHttpClient
import pl.droidsonroids.retrofit2.JspoonConverterFactory
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.time.Duration

/**
 * The interface which defines the basic scraper framework.
 *
 * @author Shea Smith
 */
abstract class Scraper {
    /**
     * Run the scraper.
     *
     * @return The result of the scraper, containing retailer, store and product information.
     */
    abstract suspend fun runScraper(): ScraperResult

    /**
     * Generate an instance of an API service that will return a JSON response
     *
     * @param cls The class of the API service to generate.
     * @param baseUrl The base URL of the request, which all API endpoint definitions are relative to.
     * @param T The type of the API service to generate.
     * @return An instance of the API service.
     */
    protected fun <T> generateJsonRequest(cls: Class<T>, baseUrl: String): T {
        val moshi = getMoshi()

        return getRetrofit(cls, baseUrl, MoshiConverterFactory.create(moshi))
    }

    /**
     * Generate an instance of an API service that will return a HTML response
     *
     * @param cls The class of the API service to generate.
     * @param baseUrl The base URL of the request, which all API endpoint definitions are relative to.
     * @param T The type of the API service to generate.
     * @return An instance of the API service.
     */
    protected fun <T> generateHtmlRequest(cls: Class<T>, baseUrl: String): T {
        return getRetrofit(cls, baseUrl, JspoonConverterFactory.create())
    }

    /**
     * Get an instance of a Retrofit service with the addons we need.
     *
     * @param cls The class of the API service to generate.
     * @param baseUrl The base URL of the request, which all API endpoint definitions are relative to.
     * @param T The type of the API service to generate.
     * @param converterFactory The converter factory to use (typically a JSON or HTML response parser).
     * @return An instance of the API service.
     */
    private fun <T> getRetrofit(
        cls: Class<T>,
        baseUrl: String,
        converterFactory: Converter.Factory
    ): T {
        val client = OkHttpClient.Builder()
            .readTimeout(Duration.ofMinutes(1))
            .writeTimeout(Duration.ofMinutes(1))
            .connectTimeout(Duration.ofMinutes(1))
            .addInterceptor(RetrofitRetryInterceptor())
            .build()

        val retrofit = Retrofit.Builder()
            .client(client)
            .addConverterFactory(converterFactory)
            .baseUrl(baseUrl)
            .build()

        return retrofit.create(cls)
    }

    /**
     * A utility method to extract and remove a quantity from a specific string.
     *
     * @param name The string to search and remove the quantity from.
     * @param unit The unit to extract and remove.
     * @return A pair, containing the new name and the extracted quantity (if any).
     */
    protected fun extractAndRemoveQuantity(name: String, unit: Units): Pair<String, Double?> {
        val capture = unit.regex.find(name)?.groups?.get(1)?.value?.toDouble()
        val newName = name.replace(unit.regex, "").replace(Regex("\\s+"), " ").trim()

        return Pair(newName, capture)
    }
}