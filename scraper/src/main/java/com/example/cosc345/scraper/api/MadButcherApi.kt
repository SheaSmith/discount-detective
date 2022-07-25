package com.example.cosc345.scraper.api

import com.example.cosc345.scraper.models.madbutcher.MadButcherProductsListResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Defines the supplemental Mad Butcher API, which despite using WooCommerce, needs extra information around the sale type of the product (for example, whether it is sold by weight, or individually).
 *
 * @author Shea Smith
 */
interface MadButcherApi {
    /**
     * Get a list of products for this page.
     *
     * @param page The page number to request.
     * @return A response, containing a list of product IDs and whether the product is sold per-kg or not, along with the max page.
     */
    @GET(".")
    suspend fun getProductsListing(@Query("product-page") page: Int): MadButcherProductsListResponse
}