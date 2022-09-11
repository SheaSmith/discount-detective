package com.example.cosc345.scraper.api

import com.example.cosc345.scraper.models.woocommerce.WooCommerceProduct
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Defines how to get information from WooCommerce based shops.
 */
interface WooComAPI {
    /**
     * Get a list of products from the shop.
     *
     * @param page The page number to query.
     * @return A list of products.
     */
    @GET("wp-json/wc/store/products?per_page=100")
    suspend fun getProducts(@Query("page") page: Int): Array<WooCommerceProduct>


}