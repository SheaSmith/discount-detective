package com.example.cosc345.scraper.api

import com.example.cosc345.scraper.models.wooCom.wooComProduct
import retrofit2.http.GET
import retrofit2.http.Query


interface WooComAPI {
    /**
     * Need to modify this to query page num
     */
    @GET("wp-json/wc/store/products?per_page=100")
    suspend fun getProducts(@Query("page") page: Int): Array<wooComProduct>


}