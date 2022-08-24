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

    /**
     * Get the best non-local price for a particular product.
     *
     * @param retailers The map of retailers to determine whether a price is local or not.
     * @return A pair, with the first item being the dollar component of the price, and the second being the cents and the sale type.
     */
    fun getBestNonLocalPrice(retailers: Map<String, Retailer>): Pair<String, String>? {
        val localRetailers = retailers.filter { it.value.local != true }.keys

        val nonLocalPrices = information!!.filter { localRetailers.contains(it.retailer) }

        return findLowestPrice(nonLocalPrices)
    }

    /**
     * Find the lowest prices based on a filtered subset of the products for this product.
     *
     * @param products The products to find the lowest price of.
     * @return A pair, with the dollars component (e.g. "$10" for $10.00/kg) as the first value, and
     * the cents component (for example, ".00/kg" for "$10.00/kg) as the second value.
     */
    private fun findLowestPrice(products: List<RetailerProductInformation>): Pair<String, String>? {
        if (products.isNotEmpty()) {
            val lowestPricePair =
                products.flatMap { productInfo -> productInfo.pricing!!.map { it to productInfo } }
                    .minBy { it.first.getPrice(it.second) }

            return lowestPricePair.first.getDisplayPrice(lowestPricePair.second)
        }

        return null
    }
}
