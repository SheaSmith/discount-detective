package com.example.cosc345.scraper.api

import com.example.cosc345.scraper.models.shopify.ShopifyResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Defines how to get products from Shopify based stores.
 *
 * @author Shea Smith
 */
interface ShopifyApi {
    /**
     * Get a list of products, with pagination.
     *
     * @param page The page that should be requested.
     * @return A response containing a list of products.
     */
    @GET("products.json?limit=250")
    suspend fun getProducts(@Query("page") page: Int): ShopifyResponse
}