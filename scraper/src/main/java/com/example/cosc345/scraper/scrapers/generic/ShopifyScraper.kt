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
        shopifyService.getProducts().products
            .forEach { shopifyProduct ->
                val product = RetailerProductInformation(retailer = id, id = shopifyProduct.id)

                // Parse any weights from the product
                val weightGrams = extractWeight(Weight.GRAMS.regex, shopifyProduct)
                val weightKilograms = extractWeight(Weight.KILOGRAMS.regex, shopifyProduct)

                if (weightGrams != 0.0) {
                    product.weight = weightGrams.toInt()
                    product.quantity = "${weightGrams}${Weight.GRAMS}"
                } else if (weightKilograms != 0.0) {
                    product.weight = (weightKilograms * 1000).toInt()
                    product.quantity = "${weightKilograms}${Weight.KILOGRAMS}"
                } else {
                    product.weight = null
                    product.quantity = null
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
                        product.quantity = firstVariant.title.lowercase(LocaleConstants.NEW_ZEALAND)
                    }
                }

                // Strip out the weight from the title if it still exists
                titleFormatted = titleFormatted
                    .replace(Weight.GRAMS.regex, "")
                    .replace(Weight.KILOGRAMS.regex, "")

                // Strip out the brand name, assuming that it has been set for the product
                titleFormatted = titleFormatted.replace(shopifyProduct.vendor, "")

                // Remove extra punctuation
                titleFormatted = titleFormatted.replace("-|\\(\\s*\\)".toRegex(), "")

                // Remove extra spaces
                titleFormatted = titleFormatted.replace("\\s{2,}".toRegex(), " ")

                product.name = titleFormatted

                // Set brand if the vendor from Shopify is not the name of the retailer
                if (shopifyProduct.vendor != retailer.name && retailer.stores?.any { it.name == shopifyProduct.vendor } != true) {
                    product.brandName = shopifyProduct.vendor
                }

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

        return ScraperResult(retailer, products)
    }

    private fun extractWeight(regex: Regex, shopifyProduct: ShopifyProduct): Double {
        return (
                // Check if there is a weight in the title
                regex.matchEntire(shopifyProduct.title)?.groups?.get(1)?.value?.toDouble() ?:
                // Check if it is in the first variant title
                regex.matchEntire(shopifyProduct.variants.first().title)?.groups?.get(1)?.value?.toDouble()
                ?: 0.0)
    }
}