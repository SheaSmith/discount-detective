package com.example.cosc345.scraper.scrapers

import com.example.cosc345.scraper.api.WarehouseApi
import com.example.cosc345.scraper.interfaces.Scraper
import com.example.cosc345.scraper.models.ScraperResult
import com.example.cosc345.shared.models.*

/**
 * The scraper for the bespoke The Warehouse shop.
 *
 * # Process
 * A list of stores is requested from The Warehouse API. These are then iterated, and for each store the products for a set of hardcoded categories is requested.
 *
 * The store is included in the request to ensure inventory is returned for the specific store.
 *
 * Each product is then processed and cleaned up as normal.
 *
 * @constructor Create a new instance of this scraper.
 */
class WarehouseScraper : Scraper() {
    override suspend fun runScraper(): ScraperResult {
        val warehouseService =
            generateJsonRequest(WarehouseApi::class.java, "https://twg.azure-api.net")
        val retailerId = "warehouse"

        val stores = mutableListOf<Store>()
        val products = mutableListOf<RetailerProductInformation>()

        val storeWhitelist = mapOf(
            "South Dunedin" to Region.DUNEDIN,
            "Invercargill" to Region.INVERCARGILL
        )
        warehouseService.getStores().stores.forEach { (branchId, storeName, latitude, longitude, _, address) ->
            val name = storeName.split(" - ").first().trim()
            if (storeWhitelist.contains(name)) {
                val store = Store(
                    id = branchId,
                    name = name,
                    address = address.address,
                    latitude = latitude,
                    longitude = longitude,
                    automated = true,
                    region = storeWhitelist[name]
                )
                stores.add(store)

                val categories = arrayOf("foodhouseholdpets")
                categories.forEach { category ->
                    var start = 0
                    var total = 0

                    while (start - 200 < total) {
                        val response =
                            warehouseService.getProducts(start, category, branchId)

                        response.products.forEach { (id, originalProductName, imageUrl, barcode, brand, priceInfo, inventory, promotions) ->
                            var product = products.firstOrNull { it.id == id }

                            if (product == null) {
                                product = RetailerProductInformation(
                                    retailer = retailerId,
                                    id = id,
                                    brandName = brand,
                                    saleType = SaleType.EACH,
                                    barcodes = listOf(barcode),
                                    image = imageUrl,
                                    automated = true,
                                    verified = false
                                )

                                var productName = originalProductName
                                    .replace(brand, "", true)
                                    .trim()

                                val weightInGrams =
                                    Units.GRAMS.regex.find(productName)?.groups?.get(1)?.value?.toDouble()
                                        ?.toInt()
                                val weightInKilograms =
                                    Units.KILOGRAMS.regex.find(productName)?.groups?.get(1)?.value?.toDouble()

                                product.weight =
                                    weightInGrams ?: weightInKilograms?.times(1000)?.toInt()

                                productName = productName
                                    .replace(Units.GRAMS.regex, "")
                                    .replace(Units.KILOGRAMS.regex, "")
                                    .replace(Regex("\\s+"), " ")
                                    .trim()

                                if (productName.isEmpty()) {
                                    productName = brand
                                    product.brandName = null
                                }

                                product.name = productName
                                product.quantity =
                                    if (weightInGrams != null) "${weightInGrams}${Units.GRAMS}" else "${weightInKilograms}${Units.KILOGRAMS}"
                            }

                            if (inventory.available && product.pricing?.none { it.store == branchId } != false) {
                                if (product.pricing == null) {
                                    product.pricing = mutableListOf()
                                    products.add(product)
                                }

                                val pricing = StorePricingInformation(
                                    store = branchId,
                                    price = if (priceInfo.date == null) priceInfo.price.times(
                                        100
                                    ).toInt() else null,
                                    discountPrice = if (priceInfo.date != null) priceInfo.price.times(
                                        100
                                    ).toInt() else null,
                                    verified = false,
                                    automated = true
                                )

                                val discount =
                                    promotions.firstOrNull { it.price != null }

                                if (discount != null) {
                                    pricing.discountPrice = discount.price?.times(100)?.toInt()

                                    if (pricing.discountPrice == pricing.price)
                                        pricing.price = null
                                }

                                val multiBuy =
                                    promotions.firstOrNull { it.association != null }
                                if (multiBuy != null) {
                                    pricing.multiBuyQuantity =
                                        (multiBuy.association?.quantity1
                                            ?: 0) + (multiBuy.association?.quantity2 ?: 0)

                                    if (pricing.multiBuyQuantity != 0) {
                                        pricing.multiBuyPrice =
                                            multiBuy.association?.amount?.times(100)?.toInt()
                                    } else {
                                        pricing.multiBuyQuantity = null
                                    }
                                }

                                product.pricing!!.add(pricing)
                            }
                        }

                        total = response.total
                        start += 200
                    }
                }
            }
        }

        val retailer = Retailer(
            name = "The Warehouse",
            automated = true,
            verified = false,
            stores = stores,
            colourLight = 0xFFffdad4,
            onColourLight = 0xFF410000,
            colourDark = 0xFF930000,
            onColourDark = 0xFFffdad4,
            initialism = "WH",
            local = false
        )

        return ScraperResult(retailer, products, retailerId)
    }
}