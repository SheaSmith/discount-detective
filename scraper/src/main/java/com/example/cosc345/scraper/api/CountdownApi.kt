package com.example.cosc345.scraper.api

import com.example.cosc345.scraper.models.countdown.CountdownSetStoreRequest
import com.example.cosc345.scraper.models.countdown.departments.CountdownDepartment
import com.example.cosc345.scraper.models.countdown.products.CountdownProductsResponse
import com.example.cosc345.scraper.models.countdown.stores.CountdownSitesResponse
import retrofit2.http.*

/**
 * The definition for the Countdown API for retrieving information from Countdown's online shopping system.
 *
 * @author Shea Smith
 */
interface CountdownApi {
    /**
     * Get a list of all Countdown stores.
     *
     * @return A response, containing the list of stores.
     */
    @GET("https://api.cdx.nz/site-location/api/v1/sites/")
    suspend fun getStores(): CountdownSitesResponse

    /**
     * Set the store for retrieving the product information for.
     *
     * @param setStoreRequest A body containing the ID of the store that we want to retrieve information for.
     */
    @Headers(
        "X-Requested-With: OnlineShopping.WebApp",
        "User-Agent: Supermarket Comparison",
        "Accept: */*"
    )
    @PUT("api/v1/fulfilment/my/pickup-addresses")
    suspend fun setStore(
        @Body setStoreRequest: CountdownSetStoreRequest
    )

    /**
     * Get the list of the different departments (for example, fruit and vegetables), as we use this to split up our requests.
     *
     * @return A list of departments.
     */
    @Headers(
        "X-Requested-With: OnlineShopping.WebApp",
        "User-Agent: Supermarket Comparison",
        "Accept: */*"
    )
    @GET("api/v1/products/departments")
    suspend fun getDepartments(): Array<CountdownDepartment>

    /**
     * Get a list of products for this specific store, and department.
     *
     * @param page The page number we wish to request, as the responses are paginated with 120 items per page.
     * @param departmentFilter The special filter string for filter by department. Should be in the format
     * @return The response, containing a list of products.
     */
    @Headers(
        "X-Requested-With: OnlineShopping.WebApp",
        "User-Agent: Supermarket Comparison",
        "Accept: */*"
    )
    @GET("api/v1/products?target=browse&size=120")
    suspend fun getProducts(
        @Query("page") page: Int,
        @Query("dasFilter") departmentFilter: String,
    ): CountdownProductsResponse
}