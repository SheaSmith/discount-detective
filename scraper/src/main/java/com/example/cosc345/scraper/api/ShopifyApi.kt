package com.example.cosc345.scraper.api

import com.example.cosc345.scraper.models.shopify.ShopifyResponse
import retrofit2.http.GET
import retrofit2.http.Query


interface ShopifyApi {
    @GET("products.json?limit=250")
    suspend fun getProducts(@Query("page") page: Int): ShopifyResponse
}