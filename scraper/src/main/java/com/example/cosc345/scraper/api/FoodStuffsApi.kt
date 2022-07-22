package com.example.cosc345.scraper.api

import com.example.cosc345.scraper.models.foodstuffs.FoodStuffsSearchRequest
import com.example.cosc345.scraper.models.foodstuffs.categories.FoodStuffsCategoryResponse
import com.example.cosc345.scraper.models.foodstuffs.products.FoodStuffsProductResponse
import com.example.cosc345.scraper.models.foodstuffs.stores.FoodStuffsStoresResponse
import com.example.cosc345.scraper.models.foodstuffs.token.FoodStuffsRefreshTokenRequest
import com.example.cosc345.scraper.models.foodstuffs.token.FoodStuffsRefreshTokenResponse
import retrofit2.http.*

interface FoodStuffsApi {
    @POST("prod/mobile/v1/users/login/refreshtoken")
    suspend fun refreshToken(@Body body: FoodStuffsRefreshTokenRequest): FoodStuffsRefreshTokenResponse

    @GET("prod/mobile/store")
    suspend fun getStores(@Header("Authorization") auth: String): FoodStuffsStoresResponse

    @POST("https://6q1kn3c1gb-dsn.algolia.net/1/indexes/{index}/query")
    suspend fun getProducts(
        @Path("index") index: String,
        @Body body: FoodStuffsSearchRequest
    ): FoodStuffsProductResponse

    @POST("https://6q1kn3c1gb-dsn.algolia.net/1/indexes/{index}/query")
    suspend fun getCategories(
        @Path("index") index: String,
        @Body body: FoodStuffsSearchRequest
    ): FoodStuffsCategoryResponse
}