package com.example.cosc345.scraper.api

import com.example.cosc345.scraper.models.veggieboys.VeggieBoysProductDetail
import com.example.cosc345.scraper.models.veggieboys.VeggieBoysProductsResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface VeggieBoysApi {
    @GET("products")
    suspend fun getProducts(): VeggieBoysProductsResponse

    @GET("product/{id}")
    suspend fun getProductDetails(@Path("id") productId: String): VeggieBoysProductDetail
}