package com.example.cosc345.scraper.api

import com.example.cosc345.scraper.model.shopify.ShopifyProduct
import retrofit2.http.GET
import retrofit2.http.Url

interface ShopifyApi {
    @GET
    fun getProducts(@Url url: String): List<ShopifyProduct>
}