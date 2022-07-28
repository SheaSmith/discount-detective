package com.example.cosc345.scraper.api

import com.example.cosc345.scraper.models.wixstores.products.WixStoresProductsRequest
import com.example.cosc345.scraper.models.wixstores.products.WixStoresProductsResponse
import com.example.cosc345.scraper.models.wixstores.token.WixStoresTokenResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Defines how to get information from WixStores based stores.
 *
 * @author Shea Smith
 */
interface WixStoresApi {
    /**
     * Get the access token we need for accessing the WixStores API.
     *
     * @return A response, containing many access tokens, including the one for WixStores. This seems to be the token with the UUID of `1380b703-ce81-ff05-f115-39571d94dfcd`.
     */
    @GET("_api/v2/dynamicmodel")
    suspend fun getToken(): WixStoresTokenResponse

    /**
     * Get a list of products from WixStores.
     *
     * @param body The request body, including the query for what information we want to extract.
     * @param token The access token we need to access this endpoint.
     * @return A response, containing a list of products.
     */
    @POST("_api/wixstores-graphql-server/graphql")
    suspend fun getProducts(
        @Body body: WixStoresProductsRequest,
        @Header("Authorization") token: String
    ): WixStoresProductsResponse
}