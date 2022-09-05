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
 * @author Shea Smith
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
        warehouseService.getStores().stores.forEach { warehouseStore ->
            val name = warehouseStore.name.split(" - ").first().trim()
            if (storeWhitelist.contains(name)) {
                val store = Store(
                    id = warehouseStore.branchId,
                    name = name,
                    address = warehouseStore.address.address,
                    latitude = warehouseStore.latitude,
                    longitude = warehouseStore.longitude,
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
                            warehouseService.getProducts(start, category, warehouseStore.branchId)

                        response.products.forEach { warehouseProduct ->
                            var product = products.firstOrNull { it.id == warehouseProduct.id }

                            if (product == null) {
                                product = RetailerProductInformation(
                                    retailer = retailerId,
                                    id = warehouseProduct.id,
                                    brandName = warehouseProduct.brand,
                                    saleType = SaleType.EACH,
                                    barcodes = listOf(warehouseProduct.barcode),
                                    image = warehouseProduct.imageUrl,
                                    automated = true,
                                    verified = false
                                )

                                var productName = warehouseProduct.name
                                    .replace(warehouseProduct.brand, "", true)
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
                                    productName = warehouseProduct.brand
                                    product.brandName = null
                                }

                                product.name = productName
                                product.quantity =
                                    if (weightInGrams != null) "${weightInGrams}${Units.GRAMS}" else "${weightInKilograms}${Units.KILOGRAMS}"
                            }

                            if (warehouseProduct.inventory.available && product.pricing?.none { it.store == warehouseStore.branchId } != false) {
                                if (product.pricing == null) {
                                    product.pricing = mutableListOf()
                                    products.add(product)
                                }

                                val pricing = StorePricingInformation(
                                    store = warehouseStore.branchId,
                                    price = if (warehouseProduct.priceInfo.date == null) warehouseProduct.priceInfo.price.times(
                                        100
                                    ).toInt() else null,
                                    discountPrice = if (warehouseProduct.priceInfo.date != null) warehouseProduct.priceInfo.price.times(
                                        100
                                    ).toInt() else null,
                                    verified = false,
                                    automated = true
                                )

                                val discount =
                                    warehouseProduct.promotions.firstOrNull { it.price != null }

                                if (discount != null) {
                                    pricing.discountPrice = discount.price?.times(100)?.toInt()

                                    if (pricing.discountPrice == pricing.price)
                                        pricing.price = null
                                }

                                val multiBuy =
                                    warehouseProduct.promotions.firstOrNull { it.association != null }
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