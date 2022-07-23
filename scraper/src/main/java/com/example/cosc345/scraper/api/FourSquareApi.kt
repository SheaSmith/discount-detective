package com.example.cosc345.scraper.api

import com.example.cosc345.scraper.models.foursquare.FourSquareStore
import com.example.cosc345.scraper.models.foursquare.mailer.FourSquareMailer
import com.example.cosc345.scraper.models.foursquare.mailer.FourSquareMailerProduct
import com.example.cosc345.scraper.models.foursquare.specials.FourSquareLocalSpecialsResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path

interface FourSquareApi {
    @GET("BrandsApi/BrandsStore/GetBrandStores")
    suspend fun getStores(): Array<FourSquareStore>

    @GET("https://app.redpepperdigital.net/client/{mailerId}/catalogues/json?_format=json")
    @Headers("User-Agent: Mozilla/ (Windows NT 10.0; Win64; x64; rv:1)")
    suspend fun getMailers(@Path("mailerId") mailerId: String): Array<FourSquareMailer>

    @GET("https://app.redpepperdigital.net/rpms_catalogue/{mailerIssueId}/page/0/100/regions")
    @Headers("User-Agent: Mozilla/ (Windows NT 10.0; Win64; x64; rv:1)")
    suspend fun getProductsForMailer(@Path("mailerIssueId") mailerIssueId: String): Map<String, Array<FourSquareMailerProduct>>

    @GET("specials-and-promotions/local-specials")
    suspend fun getLocalSpecials(@Header("Cookie") cookie: String): FourSquareLocalSpecialsResponse
}