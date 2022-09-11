package com.example.cosc345.scraper.api

import com.example.cosc345.scraper.models.robertsonsmeats.categories.RobertsonsMeatsCategoriesResponse
import com.example.cosc345.scraper.models.robertsonsmeats.products.RobertsonsMeatsProductsResponse
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Defines how to get information from Robertsons Meats bespoke ecommerce software.
 */
interface RobertsonsMeatsApi {
    /**
     * Get all of the categories that we then request the pages for.
     *
     * @return A response, containing the categories that we need to query.
     */
    @GET(".")
    suspend fun getCategories(): RobertsonsMeatsCategoriesResponse

    /**
     * Get all of the products for a specific category.
     *
     * @param href The path to the category to query.
     * @return A response, containing a list of products.
     */
    @GET("{href}")
    suspend fun getProducts(
        @Path(
            "href",
            encoded = true
        ) href: String
    ): RobertsonsMeatsProductsResponse
}