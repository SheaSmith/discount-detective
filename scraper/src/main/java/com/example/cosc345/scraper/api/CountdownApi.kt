package com.example.cosc345.scraper.api

import com.example.cosc345.scraper.models.countdown.CountdownSetStoreRequest
import com.example.cosc345.scraper.models.countdown.departments.CountdownDepartment
import com.example.cosc345.scraper.models.countdown.stores.CountdownSitesResponse
import retrofit2.http.*

interface CountdownApi {
    @GET
    suspend fun getStores(@Url url: String): CountdownSitesResponse

    @PUT
    suspend fun setStore(
        @Url url: String,
        @Body setStoreRequest: CountdownSetStoreRequest,
        @Header("X-Requested-With") requestedWith: String = "OnlineShopping.WebApp"
    )

    @GET
    suspend fun getDepartments(
        @Url url: String,
        @Header("X-Requested-With") requestedWith: String = "OnlineShopping.WebApp"
    ): Array<CountdownDepartment>

    @GET
    suspend fun getProducts(
        @Url url: String,
        @Query("page") page: Int,
        @Query("dasFilter") departmentFilter: String,
        @Query("size") size: Int = 120,
        @Query("target") target: String = "browse",
        @Header("X-Requested-With") requestedWith: String = "OnlineShopping.WebApp"
    )
}