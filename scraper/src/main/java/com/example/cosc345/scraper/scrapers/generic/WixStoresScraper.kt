package com.example.cosc345.scraper.scrapers.generic

import com.example.cosc345.scraper.api.WixStoresApi
import com.example.cosc345.scraper.interfaces.Scraper
import com.example.cosc345.scraper.models.ScraperResult
import com.example.cosc345.scraper.models.wixstores.products.WixStoresProductsRequest
import com.example.cosc345.shared.models.Retailer
import com.example.cosc345.shared.models.RetailerProductInformation
import com.example.cosc345.shared.models.SaleType
import com.example.cosc345.shared.models.StorePricingInformation

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
                    id = id,
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
                    saleType = SaleType.EACH
                )
            )
        }

        return ScraperResult(retailer, products, id)
    }
}