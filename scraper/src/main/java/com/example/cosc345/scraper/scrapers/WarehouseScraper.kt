package com.example.cosc345.scraper.scrapers

import com.example.cosc345.scraper.api.WarehouseApi
import com.example.cosc345.scraper.interfaces.Scraper
import com.example.cosc345.scraper.models.ScraperResult
import com.example.cosc345.shared.models.*

class WarehouseScraper : Scraper() {
    override suspend fun runScraper(): ScraperResult {
        val warehouseService =
            generateJsonRequest(WarehouseApi::class.java, "https://twg.azure-api.net")
        val retailerId = "warehouse"

        val stores = mutableListOf<Store>()
        val products = mutableListOf<RetailerProductInformation>()

        val storeWhitelist = arrayOf("South Dunedin")
        warehouseService.getStores().stores.forEach { warehouseStore ->
            if (storeWhitelist.contains(warehouseStore.name)) {
                val store = Store(
                    id = warehouseStore.branchId,
                    name = "The Warehouse ${warehouseStore.name}",
                    address = warehouseStore.address.address,
                    latitude = warehouseStore.latitude,
                    longitude = warehouseStore.longitude,
                    automated = true
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
                                    barcodes = setOf(warehouseProduct.barcode),
                                    image = warehouseProduct.imageUrl
                                )

                                var name = warehouseProduct.name
                                    .replace(warehouseProduct.brand, "", true)
                                    .trim()

                                val weightInGrams =
                                    Units.GRAMS.regex.find(name)?.groups?.get(1)?.value?.toDouble()
                                        ?.toInt()
                                val weightInKilograms =
                                    Units.KILOGRAMS.regex.find(name)?.groups?.get(1)?.value?.toDouble()

                                product.weight =
                                    weightInGrams ?: weightInKilograms?.times(1000)?.toInt()

                                name = name
                                    .replace(Units.GRAMS.regex, "")
                                    .replace(Units.KILOGRAMS.regex, "")
                                    .replace(Regex("\\s+"), " ")
                                    .trim()

                                if (name.isEmpty()) {
                                    name = warehouseProduct.brand
                                    product.brandName = null
                                }

                                product.name = name
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
                                    verified = true
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

        val retailer = Retailer("The Warehouse", true, stores)

        return ScraperResult(retailer, products, retailerId)
    }
}