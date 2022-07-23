package com.example.cosc345.scraper.api

import com.example.cosc345.scraper.models.wixstores.products.WixStoresProductsRequest
import com.example.cosc345.scraper.models.wixstores.products.WixStoresProductsResponse
import com.example.cosc345.scraper.models.wixstores.token.WixStoresTokenResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface WixStoresApi {
    @GET("_api/v2/dynamicmodel")
    suspend fun getToken(): WixStoresTokenResponse

    @POST("_api/wixstores-graphql-server/graphql")
    suspend fun getProducts(
        @Body body: WixStoresProductsRequest,
        @Header("Authorization") token: String
    ): WixStoresProductsResponse
}