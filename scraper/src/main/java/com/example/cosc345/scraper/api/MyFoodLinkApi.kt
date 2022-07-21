package com.example.cosc345.scraper.api

import com.example.cosc345.scraper.models.myfoodlink.MyFoodLinkProductsResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface MyFoodLinkApi {
    @GET
    suspend fun getProducts(
        @Url url: String,
        @Query("page") page: Int,
        @Query("aisle_layout") layout: String = "line",
        @Query("q[]") query: String = "category:all"
    ): MyFoodLinkProductsResponse
}