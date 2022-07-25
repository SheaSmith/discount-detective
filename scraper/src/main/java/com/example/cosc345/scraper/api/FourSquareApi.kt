package com.example.cosc345.scraper.api

import com.example.cosc345.scraper.models.foursquare.FourSquareStore
import com.example.cosc345.scraper.models.foursquare.mailer.FourSquareMailer
import com.example.cosc345.scraper.models.foursquare.mailer.FourSquareMailerProduct
import com.example.cosc345.scraper.models.foursquare.specials.FourSquareLocalSpecialsResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path

/**
 * The definitions for retrieving data from Four Square.
 *
 * @author Shea Smith
 */
interface FourSquareApi {
    /**
     * Get the lists of stores, including their region code.
     *
     * @return A list of stores.
     */
    @GET("BrandsApi/BrandsStore/GetBrandStores")
    suspend fun getStores(): Array<FourSquareStore>

    /**
     * Get a list of the current mailers. Usually this should just be one for the current time period.
     *
     * @param mailerId The ID of the mailer. This is different for the North and the South Island, and is therefore determined based on the stores region code.
     * @return A list of mailers.
     */
    @GET("https://app.redpepperdigital.net/client/{mailerId}/catalogues/json?_format=json")
    @Headers("User-Agent: Mozilla/ (Windows NT 10.0; Win64; x64; rv:1)")
    suspend fun getMailers(@Path("mailerId") mailerId: String): Array<FourSquareMailer>

    /**
     * Get all of the products from a particular mailer issue. This endpoint also returns other "interactive" items in the mailer (for example, social media links), so it needs to be filtered to just show products.
     *
     * @param mailerIssueId The issue ID of the mailer. This changes per-week, and is also different for the North and South Island.
     * @return A map, with the page number as the key, and a list of products at the value.
     */
    @GET("https://app.redpepperdigital.net/rpms_catalogue/{mailerIssueId}/page/0/100/regions")
    @Headers("User-Agent: Mozilla/ (Windows NT 10.0; Win64; x64; rv:1)")
    suspend fun getProductsForMailer(@Path("mailerIssueId") mailerIssueId: String): Map<String, Array<FourSquareMailerProduct>>

    /**
     * Get the local specials page. Currently this shows price rollback prices. This is different for the North and South Islands, and so is rendered based on the cookie.
     *
     * @param cookie The cookie to determine which page to show. This is based on the region key returned above, in the format `region_code=SI` for example.
     * @return A response, containing a list of products.
     */
    @GET("specials-and-promotions/local-specials")
    suspend fun getLocalSpecials(@Header("Cookie") cookie: String): FourSquareLocalSpecialsResponse
}