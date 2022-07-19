package com.example.cosc345.scraper.api

import com.example.cosc345.scraper.models.shopify.ShopifyProduct
import com.example.cosc345.scraper.models.shopify.ShopifyResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface ShopifyApi {
    @GET("products.json?limit=250")
    suspend fun getProducts(): ShopifyResponse
}