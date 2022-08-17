package com.example.cosc345.shared.models

/**
 * The grouping object which contains the retailer specific product information.
 *
 * @author Shea Smith
 * @constructor Create a new instance of this object. Some of the nullable parameters are not nullable in practice, but are required to be for Firebase.
 */
data class Product(
    /**
     * The list of information from different retailers for this product.
     */
    var information: MutableList<RetailerProductInformation>? = null,
) {
    fun getBestInformation(): RetailerProductInformation {
        return information!!.sortedWith(
            Comparator { t: RetailerProductInformation, t2: RetailerProductInformation ->
                return@Comparator t.getRetailerScore() - t2.getRetailerScore()
            }
        ).first()
    }

    /**
     * Get the best local price for a particular product.
     *
     * @param retailers The map of retailers to determine whether a price is local or not.
     * @return A pair, with the first item being the dollar component of the price, and the second being the cents and the sale type.
     */
    fun getBestLocalPrice(retailers: Map<String, Retailer>): Pair<String, String>? {
        val localRetailers = retailers.filter { it.value.local == true }.keys

        val localPrices = information!!.filter { localRetailers.contains(it.retailer) }

        return findLowestPrice(localPrices)
    }

    fun getBestNonLocalPrice(retailers: Map<String, Retailer>): Pair<String, String>? {
        val localRetailers = retailers.filter { it.value.local != true }.keys

        val nonLocalPrices = information!!.filter { localRetailers.contains(it.retailer) }

        return findLowestPrice(nonLocalPrices)
    }

    private fun findLowestPrice(products: List<RetailerProductInformation>): Pair<String, String>? {
        if (products.isNotEmpty()) {
            val lowestPricePair =
                products.flatMap { productInfo -> productInfo.pricing!!.map { it to productInfo } }
                    .minBy { it.first.getPrice(it.second) }

            val lowestPrice =
                lowestPricePair.first.getPrice(lowestPricePair.second).toString()

            val salePrefix = if (lowestPricePair.second.saleType == SaleType.WEIGHT) {
                "kg"
            } else {
                "ea"
            }

            val dollarComponent = "$${lowestPrice.substring(0, lowestPrice.length - 2)}"
            val centsComponent =
                ".${
                    lowestPrice.substring(
                        lowestPrice.length - 2,
                        lowestPrice.length
                    )
                }/${salePrefix}"

            return Pair(dollarComponent, centsComponent)
        }

        return null
    }
}
