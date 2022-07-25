package com.example.cosc345.scraper.scrapers.generic

import com.example.cosc345.scraper.api.FoodStuffsApi
import com.example.cosc345.scraper.interfaces.Scraper
import com.example.cosc345.scraper.models.ScraperResult
import com.example.cosc345.scraper.models.foodstuffs.FoodStuffsSearchRequest
import com.example.cosc345.scraper.models.foodstuffs.promotions.FoodStuffsPromotion
import com.example.cosc345.scraper.models.foodstuffs.token.FoodStuffsRefreshTokenRequest
import com.example.cosc345.shared.models.*

abstract class FoodStuffsScraper(
    private val id: String,
    private val retailer: Retailer,
    private val productsIndex: String,
    private val categoriesIndex: String,
    private val refreshToken: String,
    private val storeWhiteList: Array<String>
) : Scraper() {
    override suspend fun runScraper(): ScraperResult {
        val foodStuffsService =
            generateJsonRequest(FoodStuffsApi::class.java, "https://api-prod.prod.fsniwaikato.kiwi")

        val products = mutableListOf<RetailerProductInformation>()

        val token =
            foodStuffsService.refreshToken(FoodStuffsRefreshTokenRequest(refreshToken)).accessToken

        val foodStuffsStores = foodStuffsService.getStores("Bearer $token").stores.filter {
            storeWhiteList.contains(it.name)
        }

        val stores = foodStuffsStores.map {
            Store(
                id = it.id,
                name = it.name,
                address = it.address,
                latitude = it.latitude,
                longitude = it.longitude,
                automated = true
            )
        }

        val allCategories = foodStuffsService.getCategories(
            categoriesIndex,
            FoodStuffsSearchRequest(facetFilters = arrayOf())
        ).categories

        val foodStuffsCategories = allCategories.filter { it.level == 1 }.toMutableList()

        var i = 0
        while (i < foodStuffsCategories.size) {
            val foodStuffsCategory = foodStuffsCategories[i]
            i++

            val response = foodStuffsService.getProducts(
                productsIndex, FoodStuffsSearchRequest(
                    facetFilters = arrayOf(
                        arrayOf("category${foodStuffsCategory.level}:${foodStuffsCategory.categoryId}")
                    )
                )
            )

            // We have greater than or equal to 1000 results, so some have been truncated. We therefore have to search on subcategories instead.
            if (response.numberOfResults >= 1000) {

                foodStuffsCategories.addAll(allCategories.filter {
                    foodStuffsCategory.childrenCategories.contains(
                        it.id
                    )
                })
                continue
            }

            val productDiscountsToCheck = mutableMapOf<String, MutableList<String>>()

            response.products.filter { foodStuffsProduct -> products.none { it.id == foodStuffsProduct.productId } }
                .forEach { foodStuffsProduct ->
                    val product = RetailerProductInformation(
                        retailer = id,
                        id = foodStuffsProduct.productId,
                        name = foodStuffsProduct.name,
                        brandName = foodStuffsProduct.brand,
                        saleType = if (foodStuffsProduct.saleType == "WEIGHT") SaleType.WEIGHT else SaleType.EACH,
                        weight = if (foodStuffsProduct.saleType == "WEIGHT") 1000 else null,
                        quantity = if (foodStuffsProduct.saleType != "WEIGHT") foodStuffsProduct.netContentDisplay else null,
                        image = "https://a.fsimg.co.nz/product/retail/fan/image/500x500/${
                            foodStuffsProduct.productId.split(
                                "-"
                            )[0]
                        }.png"
                    )

                    if (foodStuffsProduct.barcodes.isNotBlank()) {
                        val barcodes =
                            foodStuffsProduct.barcodes.split(",").filter { it.length > 7 }.toSet()
                        product.barcodes = barcodes
                    }

                    if (product.weight == null) {
                        var weight = foodStuffsProduct.netContent?.toDouble()

                        if (foodStuffsProduct.netContentUnit == Units.KILOGRAMS.toString()) {
                            weight = weight?.times(1000)
                            product.weight = weight?.toInt()
                        } else if (foodStuffsProduct.netContentUnit == Units.GRAMS.toString()) {
                            product.weight = weight?.toInt()
                        }
                    }

                    val prices =
                        foodStuffsProduct.prices.filter { price -> foodStuffsStores.any { it.idWithoutDashes == price.key } }

                    if (prices.isNotEmpty()) {
                        val discountsToCheck =
                            foodStuffsProduct.promotionStart.keys.filter { discountStore -> foodStuffsStores.any { it.id == discountStore } }

                        discountsToCheck.forEach {
                            if (!productDiscountsToCheck.containsKey(it))
                                productDiscountsToCheck[it] = mutableListOf()

                            productDiscountsToCheck[it]?.add(foodStuffsProduct.productId)
                        }

                        product.pricing = prices.map { priceMap ->
                            StorePricingInformation(
                                store = foodStuffsStores.first { it.idWithoutDashes == priceMap.key }.id,
                                price = priceMap.value.toDouble().times(100).toInt(),
                                verified = true
                            )
                        }.toMutableList()
                    }

                    if (product.pricing != null)
                        products.add(product)
                }

            productDiscountsToCheck.forEach { discountMap ->
                foodStuffsService.getPromotions(
                    discountMap.key,
                    discountMap.value.joinToString(",")
                ).promotions.forEach { foodStuffsPromotion ->

                    val price =
                        products.first { it.id == foodStuffsPromotion.productId }.pricing!!.first { it.store == discountMap.key }

                    parsePromotion(foodStuffsPromotion, price, false)
                }
            }
        }

        retailer.stores = stores

        return ScraperResult(retailer, products)
    }

    private fun parsePromotion(
        promotion: FoodStuffsPromotion,
        price: StorePricingInformation,
        clubOnly: Boolean
    ) {
        if (promotion.price != 0.0 && promotion.quantity == 1) {
            price.discountPrice = promotion.price.times(100).toInt()

            if (price.price == price.discountPrice) {
                price.price = null
            }
        } else if (promotion.quantity > 1) {
            price.multiBuyQuantity = promotion.quantity
            price.multiBuyPrice = promotion.price.times(100).toInt()
        }

        if (promotion.price != 0.0) {
            price.clubOnly = clubOnly
        }

        if (promotion.loyaltyPromotion != null) {
            parsePromotion(promotion.loyaltyPromotion, price, true)
        }
    }
}