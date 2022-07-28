package com.example.cosc345.scraper.api

import com.example.cosc345.scraper.models.foodstuffs.FoodStuffsSearchRequest
import com.example.cosc345.scraper.models.foodstuffs.categories.FoodStuffsCategoryResponse
import com.example.cosc345.scraper.models.foodstuffs.products.FoodStuffsProductResponse
import com.example.cosc345.scraper.models.foodstuffs.promotions.FoodStuffsPromotionResponse
import com.example.cosc345.scraper.models.foodstuffs.stores.FoodStuffsStoresResponse
import com.example.cosc345.scraper.models.foodstuffs.token.FoodStuffsRefreshTokenRequest
import com.example.cosc345.scraper.models.foodstuffs.token.FoodStuffsRefreshTokenResponse
import retrofit2.http.*

/**
 * The definition for FoodStuffs-based stores that defines how to retrieve data from them.
 *
 * @author Shea Smith
 */
interface FoodStuffsApi {
    /**
     * Get a new authorisation token for interacting with specific FoodStuffs APIs that require authorisation.
     *
     * @param body The body, containing the refresh token that is used to get a new auth token.
     * @return A response, containing the new access token.
     */
    @POST("prod/mobile/v1/users/login/refreshtoken")
    suspend fun refreshToken(@Body body: FoodStuffsRefreshTokenRequest): FoodStuffsRefreshTokenResponse

    /**
     * Get a list of stores for a particular FoodStuffs retailer.
     *
     * @param auth The access token from the request above. This also determines which retailers stores are returned (for example, a New World based access token will result in New World stores being returned).
     * @return A response containing the list of stores.
     */
    @GET("prod/mobile/store")
    suspend fun getStores(@Header("Authorization") auth: String): FoodStuffsStoresResponse

    /**
     * Query the products index to get the products for a particular category.
     *
     * @param index The name of the index. This is retailer-specific, so New World and PAK'nSAVE have different ones, but New World Gardens and New World Centre City have the same one.
     * @param body The request body, containing the filters that we need to ensure not too many results are returned.
     * @return A response, containing a list of products.
     */
    @POST("https://6q1kn3c1gb-dsn.algolia.net/1/indexes/{index}/query")
    @Headers(
        "x-algolia-api-key: 5c9ca0c058cbd3170b9a73605b1cc46c",
        "x-algolia-application-id: 6Q1KN3C1GB"
    )
    suspend fun getProducts(
        @Path("index") index: String,
        @Body body: FoodStuffsSearchRequest
    ): FoodStuffsProductResponse

    /**
     * Get all of the categories we will use to separate out the products queries.
     *
     * @param index The name of the index to use. This is retailer-specific, so New World and PAK'nSAVE have different ones, but New World Gardens and New World Centre City have the same one.
     * @param body The body that contains the boilerplate we need to make the query. There's no specific options needed for this particular request.
     * @return A response, containing a list of categories.
     */
    @POST("https://6q1kn3c1gb-dsn.algolia.net/1/indexes/{index}/query")
    @Headers(
        "x-algolia-api-key: 5c9ca0c058cbd3170b9a73605b1cc46c",
        "x-algolia-application-id: 6Q1KN3C1GB"
    )
    suspend fun getCategories(
        @Path("index") index: String,
        @Body body: FoodStuffsSearchRequest
    ): FoodStuffsCategoryResponse

    /**
     * Get the promotions currently ongoing for a list of products. This is useful for determining multi-buy discounts, or club-only specials, as that information is not returned by the main products request.
     *
     * @param storeId The ID of the store we are checking promotions for.
     * @param productIds A list, delimited by commas, in a string, of the product IDs that we are searching for.
     * @return A response, containing a list of promotions for the product IDs we have requested (if any).
     */
    @GET("prod/mobile/v1/promos")
    suspend fun getPromotions(
        @Query("storeId") storeId: String,
        @Query("products") productIds: String
    ): FoodStuffsPromotionResponse
}