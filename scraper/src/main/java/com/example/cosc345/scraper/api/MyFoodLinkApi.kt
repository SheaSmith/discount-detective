package com.example.cosc345.scraper.api

import com.example.cosc345.scraper.models.myfoodlink.MyFoodLinkStore
import com.example.cosc345.scraper.models.myfoodlink.products.MyFoodLinkProductsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MyFoodLinkApi {
    @GET("search?aisle_layout=line&q[]=category:all")
    suspend fun getProducts(@Query("page") page: Int): MyFoodLinkProductsResponse

    @GET("api/v1/stores")
    suspend fun getStores(): Array<MyFoodLinkStore>
}