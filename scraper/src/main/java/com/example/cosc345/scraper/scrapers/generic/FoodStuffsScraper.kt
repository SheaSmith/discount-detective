package com.example.cosc345.scraper.scrapers.generic

import com.example.cosc345.scraper.api.FoodStuffsApi
import com.example.cosc345.scraper.interfaces.Scraper
import com.example.cosc345.scraper.models.ScraperResult
import com.example.cosc345.scraper.models.foodstuffs.FoodStuffsSearchRequest
import com.example.cosc345.scraper.models.foodstuffs.promotions.FoodStuffsPromotion
import com.example.cosc345.scraper.models.foodstuffs.token.FoodStuffsRefreshTokenRequest
import com.example.cosc345.shared.models.*

/**
 * A generic scraper that scrapes from FoodStuffs based stores (for example, New World or PAK'nSAVE).
 *
 * # Process
 * Firstly, an access token must be requested in order to query a list of stores. This refresh token must be provided to the scraper.
 *
 * Once this token is retrieved, the list of stores can be retrieved. Then the list of categories is requested.
 *
 * For each top-level category a list of products is requested. If the top-level category contains more than 1000 products (a hard limit imposed by FoodStuffs), then the sub-categories for that particular category will be used instead.
 *
 * Each product is processed to extract all information from the structure, along with cleaning up the names where required, and is added to a list to check for promotions.
 *
 * As some promotions, such a club promotions for New World, are not included in the overall products request, there has to be specific requests made to find the promotions going on at particular stores. Therefore, for each category, and for each store, a request to the promotions API is made, to find out what promotions are currently active for all of the products.
 *
 * The stores and products and then passed back to the scraper implementation.
 *
 * @param id The ID of the retailer that should be used.
 * @param retailer The retailer that the stores should be added to.
 * @param productsIndex The URL path parameter that specifies which Agoria (the search engine used by FoodStuffs) index should be queried for the products request.
 * @param categoriesIndex The URL path parameter that specific which Agoria index should be queried for the categories request.
 * @param refreshToken The refresh token to use to generate an access token.
 * @param storeWhiteList The stores we should download data for, as the app is currently region locked.
 *
 * @author Shea Smith
 * @constructor Create a new instance of this scraper, for the retailer specified in the constructor.
 */
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
                        saleType = if (foodStuffsProduct.saleType != "UNITS") SaleType.WEIGHT else SaleType.EACH,
                        weight = if (foodStuffsProduct.saleType != "UNITS") 1000 else null,
                        quantity = if (foodStuffsProduct.saleType == "UNITS") foodStuffsProduct.netContentDisplay else null,
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

        return ScraperResult(retailer, products, id)
    }

    /**
     * Parse a promotion, and apply that promotion to a price object.
     */
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