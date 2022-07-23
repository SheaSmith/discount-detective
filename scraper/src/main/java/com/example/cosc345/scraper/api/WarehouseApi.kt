package com.example.cosc345.scraper.api

import com.example.cosc345.scraper.models.warehouse.products.WarehouseProductsResponse
import com.example.cosc345.scraper.models.warehouse.stores.WarehouseStoresResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface WarehouseApi {
    @GET("twlYourWarehouseProd/GetBranches.json")
    @Headers(
        "ocp-apim-subscription-key: 652e5dd7fa5a48d18f957dfeffdc2924"
    )
    suspend fun getStores(): WarehouseStoresResponse

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