package com.example.cosc345.scraper.scrapers

import com.example.cosc345.scraper.api.CountdownApi
import com.example.cosc345.scraper.interfaces.Scraper
import com.example.cosc345.scraper.models.ScraperResult
import com.example.cosc345.scraper.models.countdown.CountdownSetStoreRequest
import com.example.cosc345.shared.extensions.titleCase
import com.example.cosc345.shared.models.*

class CountdownScraper : Scraper() {
    override suspend fun runScraper(): ScraperResult {
        val countdownService =
            generateJsonRequest(CountdownApi::class.java, "https://www.countdown.co.nz")
        val retailerId = "countdown"

        val stores: MutableList<Store> = mutableListOf()
        val products: MutableList<RetailerProductInformation> = mutableListOf()
        countdownService.getStores().siteDetails
            .forEach { countdownStore ->
                if (countdownStore.site.suburb == "Dunedin") {
                    val addressList = mutableListOf(
                        countdownStore.site.addressLine1.replace(
                            ", ${countdownStore.site.suburb}",
                            ""
                        )
                    )

                    if (countdownStore.site.addressLine2 != null) {
                        addressList.add(countdownStore.site.addressLine2)
                    }

                    addressList.add(countdownStore.site.suburb)
                    addressList.add(countdownStore.site.postcode)

                    val store = Store(
                        countdownStore.site.storeId,
                        countdownStore.site.name,
                        addressList.joinToString(", "),
                        countdownStore.site.latitude,
                        countdownStore.site.longitude,
                        true
                    )
                    stores.add(store)

                    countdownService.setStore(CountdownSetStoreRequest(countdownStore.site.storeId.toInt()))

                    val departments =
                        countdownService.getDepartments().map { department -> department.url }
                    departments.forEach { countdownDepartment ->
                        var page = 1
                        // Dummy value for the first loop
                        var lastSize = 1
                        while ((page - 1) * 120 < lastSize) {
                            val response = countdownService.getProducts(
                                page,
                                "Department;;${countdownDepartment};false"
                            ).products

                            response.items.forEach { countdownProduct ->
                                if (countdownProduct.type == "Product") {
                                    var product =
                                        products.firstOrNull { it.id == countdownProduct.sku }

                                    if (product == null) {
                                        product = RetailerProductInformation(
                                            retailer = retailerId,
                                            id = countdownProduct.sku,
                                            brandName = countdownProduct.brand?.titleCase()?.trim(),
                                            variant = countdownProduct.variety?.titleCase()?.trim(),
                                            saleType = if (countdownProduct.unit == "Kg") SaleType.WEIGHT else SaleType.EACH,
                                            quantity = if (countdownProduct.unit != "Kg") countdownProduct.size?.size else null,
                                            barcodes = if (countdownProduct.barcode != null) setOf(
                                                countdownProduct.barcode
                                            ) else null,
                                            image = countdownProduct.images?.imageUrl
                                        )

                                        var title = countdownProduct.name

                                        if (countdownProduct.brand != null) {
                                            title = title.replace(countdownProduct.brand, "")
                                        }

                                        if (countdownProduct.variety != null) {
                                            title = title.replace(countdownProduct.variety, "")
                                        }

                                        product.name = title.trim().titleCase()

                                        if (product.name.isNullOrEmpty()) {
                                            product.name = product.variant
                                            product.variant = null
                                        }

                                        if (countdownProduct.unit == "Kg") {
                                            product.weight = 1000
                                        } else if (countdownProduct.size?.size != null) {
                                            product.weight =
                                                    // Try to get grams first
                                                Units.GRAMS.regex
                                                    .find(countdownProduct.size.size)
                                                    ?.groups
                                                    ?.get(1)
                                                    ?.value
                                                    // Convert to a double first, so if there is a weight like 5.4g (with 5.4 captured), there won't be an error about it not being a valid int
                                                    ?.toDouble()
                                                    // Convert to int to truncate the decimal places
                                                    ?.toInt()

                                                        // We don't have any valid grams captures, so try kilograms
                                                    ?: (Units.KILOGRAMS.regex
                                                        .find(countdownProduct.size.size)
                                                        ?.groups
                                                        ?.get(1)
                                                        ?.value
                                                        // Convert to double, e.g. for 1.5kg
                                                        ?.toDouble()
                                                        // Convert to grams
                                                        ?.times(1000))
                                                        // Convert to an int, truncating the decimal places
                                                        ?.toInt()
                                        }

                                        val checkProduct =
                                            products.firstOrNull { it.name == product!!.name && it.brandName == product!!.brandName && it.variant == product!!.variant && it.saleType == product!!.saleType && it.quantity == product!!.quantity }

                                        if (checkProduct == null)
                                            products.add(product)
                                        else
                                            product = checkProduct
                                    }

                                    if (product.pricing?.none { it.store == store.id } != false) {

                                        if (product.pricing == null)
                                            product.pricing = mutableListOf()

                                        val pricing = StorePricingInformation(
                                            store = store.id,
                                            price = countdownProduct.price?.originalPrice?.times(100)
                                                ?.toInt(),
                                            verified = true
                                        )

                                        if (countdownProduct.price?.savePrice != 0.0) {
                                            pricing.discountPrice =
                                                countdownProduct.price?.salePrice?.times(100)
                                                    ?.toInt()
                                            pricing.clubOnly = countdownProduct.price?.isClubPrice
                                        }

                                        if (countdownProduct.productTag?.multiBuy != null) {
                                            pricing.multiBuyPrice =
                                                countdownProduct.productTag.multiBuy.price.times(100)
                                                    .toInt()
                                            pricing.multiBuyQuantity =
                                                countdownProduct.productTag.multiBuy.quantity
                                        }

                                        product.pricing!!.add(pricing)
                                    }
                                }
                            }

                            lastSize = response.totalItems
                            page++
                        }
                    }
                }
            }

        val retailer = Retailer("Countdown", true, stores)

        return ScraperResult(retailer, products)
    }
}