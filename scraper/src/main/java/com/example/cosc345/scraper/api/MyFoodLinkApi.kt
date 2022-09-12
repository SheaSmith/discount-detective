package com.example.cosc345.scraper.api

import com.example.cosc345.scraper.models.myfoodlink.MyFoodLinkStore
import com.example.cosc345.scraper.models.myfoodlink.products.MyFoodLinkProductsResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Defines how to retrieve data from MyFoodLink based stores (for example, FreshChoice or SuperValue).
 */
interface MyFoodLinkApi {
    /**
     * Get a list of products, based on a paginated list.
     *
     * @param page The page number to request.
     * @return A response, containing a list of products, along with the maximum page number.
     */
    @GET("search?aisle_layout=line&q[]=category:all")
    suspend fun getProducts(@Query("page") page: Int): MyFoodLinkProductsResponse

    /**
     * Get a list of stores for this particular MyFoodLink based outlet.
     *
     * @return A list of stores.
     */
    @GET("api/v1/stores")
    suspend fun getStores(): Array<MyFoodLinkStore>
}