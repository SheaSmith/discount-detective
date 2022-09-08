package com.example.cosc345.scraper.scrapers.generic

import com.example.cosc345.scraper.api.WixStoresApi
import com.example.cosc345.scraper.interfaces.Scraper
import com.example.cosc345.scraper.models.ScraperResult
import com.example.cosc345.scraper.models.wixstores.products.WixStoresProductsRequest
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.RetailerProductInformation
import com.example.cosc345.shared.models.SaleType
import com.example.cosc345.shared.models.StorePricingInformation

/**
 * A generic scraper for all Wix Stores based retailers (for example Spelt Bakery).
 *
 * # Process
 * Again, this is a reasonably simple process. We just need to get an access token for the Wix Stores section, in order to authenticate the requests, and then use this token to make requests to the Wix Stores API. No pagination seems to be necessary.
 *
 * @param id The ID of the retailer to use.
 * @param retailer The retailer to return.
 * @param baseUrl The base URL all API requests are made relative to.
 *
 * @constructor Create a new instance of this scraper, for the retailer specified in the constructor.
 */
abstract class WixStoresScraper(
    private val id: String,
    private val retailer: Retailer,
    private val baseUrl: String
) : Scraper() {
    override suspend fun runScraper(): ScraperResult {
        val wixStoresService = generateJsonRequest(WixStoresApi::class.java, baseUrl)

        val products = mutableListOf<RetailerProductInformation>()

        val token =
            wixStoresService.getToken().tokens["1380b703-ce81-ff05-f115-39571d94dfcd"]!!.token

        wixStoresService.getProducts(
            WixStoresProductsRequest(
                "query getProductList { products(limit: 400) { id name price isInStock media { fullUrl }, ribbon } }",
            ), token
        ).data.products.filter { it.isInStock }.forEach { wixStoresProduct ->
            products.add(
                RetailerProductInformation(
                    retailer = id,
                    id = wixStoresProduct.id,
                    name = wixStoresProduct.name
                        .replace(Regex("\\s+"), " ")
                        .replace(Regex("\\s([()])"), "$1")
                        .trim(),
                    image = wixStoresProduct.media.first().imageUrl,
                    pricing = mutableListOf(
                        StorePricingInformation(
                            store = id,
                            price = wixStoresProduct.price.times(100).toInt()
                        )
                    ),
                    saleType = SaleType.EACH,
                    automated = true,
                    verified = false
                )
            )
        }

        return ScraperResult(retailer, products, id)
    }
}