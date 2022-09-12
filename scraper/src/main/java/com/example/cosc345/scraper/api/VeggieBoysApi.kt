package com.example.cosc345.scraper.api

import com.example.cosc345.scraper.models.veggieboys.VeggieBoysProductDetail
import com.example.cosc345.scraper.models.veggieboys.VeggieBoysProductsResponse
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Defines how to get information from Veggie Boys' bespoke store.
 */
interface VeggieBoysApi {
    /**
     * Get a list of products from the store.
     *
     * @return A response, containing a list of products.
     */
    @GET("products")
    suspend fun getProducts(): VeggieBoysProductsResponse

    /**
     * Get details about a specific product, verifying whether it is sold per-kg or not.
     *
     * @param productId The ID of the product to check.
     * @return Details about the product, specifically whether it is sold per-kg or not.
     */
    @GET("product/{id}")
    suspend fun getProductDetails(@Path("id") productId: String): VeggieBoysProductDetail
}