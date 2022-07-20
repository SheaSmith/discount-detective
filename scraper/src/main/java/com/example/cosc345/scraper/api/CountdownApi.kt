package com.example.cosc345.scraper.api

import com.example.cosc345.scraper.models.countdown.CountdownSetStoreRequest
import com.example.cosc345.scraper.models.countdown.departments.CountdownDepartment
import com.example.cosc345.scraper.models.countdown.products.CountdownProductsResponse
import com.example.cosc345.scraper.models.countdown.stores.CountdownSitesResponse
import retrofit2.http.*

interface CountdownApi {
    @GET
    suspend fun getStores(@Url url: String): CountdownSitesResponse

    @Headers(
        "X-Requested-With: OnlineShopping.WebApp",
        "User-Agent: Supermarket Comparison",
        "Accept: */*"
    )
    @PUT("api/v1/fulfilment/my/pickup-addresses")
    suspend fun setStore(
        @Body setStoreRequest: CountdownSetStoreRequest
    )

    @Headers(
        "X-Requested-With: OnlineShopping.WebApp",
        "User-Agent: Supermarket Comparison",
        "Accept: */*"
    )
    @GET("api/v1/products/departments")
    suspend fun getDepartments(): Array<CountdownDepartment>

    @Headers(
        "X-Requested-With: OnlineShopping.WebApp",
        "User-Agent: Supermarket Comparison",
        "Accept: */*"
    )
    @GET("api/v1/products?target=browse&size=120")
    suspend fun getProducts(
        @Query("page") page: Int,
        @Query("dasFilter") departmentFilter: String,
    ): CountdownProductsResponse
}