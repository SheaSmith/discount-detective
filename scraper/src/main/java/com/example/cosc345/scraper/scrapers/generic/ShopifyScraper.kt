package com.example.cosc345.scraper.scrapers.generic

import com.example.cosc345.scraper.api.ShopifyApi
import com.example.cosc345.scraper.interfaces.Scraper
import com.example.cosc345.scraper.models.ScraperResult
import com.example.cosc345.scraper.models.shopify.ShopifyProduct
import com.example.cosc345.shared.constants.LocaleConstants
import com.example.cosc345.shared.models.*

abstract class ShopifyScraper(
    private val id: String,
    private val retailer: Retailer,
    private val baseUrl: String
) : Scraper() {
    override suspend fun runScraper(): ScraperResult {
        val shopifyService = generateJsonRequest(ShopifyApi::class.java, baseUrl)

        val products: MutableList<RetailerProductInformation> = mutableListOf()
        var page = 1
        var lastSize = 250

        while (lastSize == 250) {
            val response = shopifyService.getProducts(page)

            response.products.forEach { shopifyProduct ->
                val product = RetailerProductInformation(retailer = id, id = shopifyProduct.id)

                // Parse any weights from the product
                Units.all.forEach {
                    val quantity = extractWeight(it.regex, shopifyProduct)

                    if (quantity != 0.0) {
                        product.quantity = "${quantity}${it}"

                        if (it == Units.GRAMS) {
                            product.weight = quantity.toInt()
                        } else if (it == Units.KILOGRAMS) {
                            product.weight = quantity.times(1000).toInt()
                        }
                    }
                }

                // We only really need to look at the first variant, as the others usually just describe different weights
                val firstVariant = shopifyProduct.variants.first()

                // If the variant, or the title contains "pack" along with a valid weight, then we should regard it as being sold as a unit, rather than per-kg
                // Otherwise if we were able to extract a weight from the product, then we should assume it is sold by weight.
                if (product.weight != null &&
                    !shopifyProduct.title.contains("pack", true) &&
                    !firstVariant.title.contains("pack", true)
                ) {
                    product.saleType = SaleType.WEIGHT
                } else {
                    product.saleType = SaleType.EACH
                }

                // Clean up title
                var titleFormatted = shopifyProduct.title

                // If the variant title has a number in it, then we should strip it out of the main product title if it exists,
                // as that means it is likely describing the quantity
                if (firstVariant.title.contains("\\d".toRegex())) {
                    titleFormatted = titleFormatted.replace(firstVariant.title, "", true)

                    // We assume that the variant is the unit, so set that if it hasn't been set already
                    if (product.quantity == null) {
                        product.quantity =
                            firstVariant.title.lowercase(LocaleConstants.NEW_ZEALAND)
                    }
                }

                // Strip out the weight from the title if it still exists
                Units.all.forEach {
                    titleFormatted = titleFormatted
                        .replace(it.regex, "")
                }

                // Strip out the brand name, assuming that it has been set for the product
                titleFormatted = titleFormatted.replace(shopifyProduct.vendor, "")

                // Remove extra punctuation
                titleFormatted = titleFormatted.replace("-|\\(\\s*\\)".toRegex(), "")

                // Remove extra spaces
                titleFormatted = titleFormatted.replace("\\s{2,}".toRegex(), " ")

                // Set brand if the vendor from Shopify is not the name of the retailer
                if (shopifyProduct.vendor != retailer.name && retailer.stores?.any { it.name == shopifyProduct.vendor } != true) {
                    product.brandName = shopifyProduct.vendor.trim()
                }

                if (titleFormatted.isBlank()) {
                    titleFormatted = shopifyProduct.vendor.trim()
                    product.brandName = null
                }

                product.name = titleFormatted.trim()

                // Set image
                product.image = shopifyProduct.images.firstOrNull()?.url

                // Set price
                product.pricing = mutableListOf(
                    StorePricingInformation(
                        id,
                        (firstVariant.price.toDouble() * 100).toInt(),
                        verified = true
                    )
                )

                products.add(product)
            }

            lastSize = response.products.size
            page++
        }

        return ScraperResult(retailer, products, id)
    }

    private fun extractWeight(regex: Regex, shopifyProduct: ShopifyProduct): Double {
        return (
                // Check if there is a weight in the title
                regex.find(shopifyProduct.title)?.groups?.get(1)?.value?.toDouble() ?:
                // Check if it is in the first variant title
                regex.find(shopifyProduct.variants.first().title)?.groups?.get(1)?.value?.toDouble()
                ?: 0.0)
    }
}