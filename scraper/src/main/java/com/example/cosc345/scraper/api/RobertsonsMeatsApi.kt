package com.example.cosc345.scraper.api

import com.example.cosc345.scraper.models.robertsonsmeats.categories.RobertsonsMeatsCategoriesResponse
import com.example.cosc345.scraper.models.robertsonsmeats.products.RobertsonsMeatsProductsResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface RobertsonsMeatsApi {
    @GET(".")
    suspend fun getCategories(): RobertsonsMeatsCategoriesResponse

    @GET("{href}")
    suspend fun getProducts(
        @Path(
            "href",
            encoded = true
        ) href: String
    ): RobertsonsMeatsProductsResponse
}