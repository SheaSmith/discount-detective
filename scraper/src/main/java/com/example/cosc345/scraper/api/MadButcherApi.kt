package com.example.cosc345.scraper.api

import com.example.cosc345.scraper.models.madbutcher.MadButcherProductsListResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MadButcherApi {
    @GET(".")
    suspend fun getProductsListing(@Query("product-page") page: Int): MadButcherProductsListResponse
}