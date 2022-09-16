package com.example.cosc345.scraper.api

import com.example.cosc345.scraper.models.warehouse.products.WarehouseProductsResponse
import com.example.cosc345.scraper.models.warehouse.stores.WarehouseStoresResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

/**
 * Defines how to get information from The Warehouse.
 */
interface WarehouseApi {
    /**
     * Get a list of all Warehouse stores.
     *
     * @return A response, containing a list of stores.
     */
    @GET("twlYourWarehouseProd/GetBranches.json")
    @Headers(
        "ocp-apim-subscription-key: 652e5dd7fa5a48d18f957dfeffdc2924"
    )
    suspend fun getStores(): WarehouseStoresResponse

    /**
     * Get a list of products for a particular category.
     *
     * @param start What index the response should start at, used for pagination.
     * @param categoryId The ID of the category to query.
     * @param branchId The ID of a store. This is used to check if the product is in stock at that particular store.
     * @return A response, containing a list of products.
     */
    @GET("twlYourWarehouseProd/CategoryProducts.json?Limit=200")
    @Headers(
        "ocp-apim-subscription-key: 652e5dd7fa5a48d18f957dfeffdc2924"
    )
    suspend fun getProducts(
        @Query("Start") start: Int,
        @Query("CategoryId") categoryId: String,
        @Query("branchId") branchId: String
    ): WarehouseProductsResponse
}