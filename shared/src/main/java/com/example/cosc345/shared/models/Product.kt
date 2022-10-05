package com.example.cosc345.shared.models

/**
 * The grouping object which contains the retailer specific product information.
 *
 * @constructor Create a new instance of this object. Some of the nullable parameters are not nullable in practice, but are required to be for Firebase.
 */
data class Product(
    /**
     * The list of information from different retailers for this product.
     */
    var information: MutableList<RetailerProductInformation>? = null,
) {
    /**
     * Get the best retailer product information that is attached to this product, based on pred
     */
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
    fun getBestLocalPrice(retailers: Map<String, Retailer>, region: String): Pair<String, String>? {
        val localRetailers =
            retailers.filter {
                it.value.local == true && it.value.stores?.any { store ->
                    store.region.equals(
                        region,
                        true
                    )
                } == true
            }.keys

        val localPrices = information!!.filter { localRetailers.contains(it.retailer) }

        return findLowestPrice(localPrices, retailers, region)
    }

    /**
     * Get the best non-local price for a particular product.
     *
     * @param retailers The map of retailers to determine whether a price is local or not.
     * @return A pair, with the first item being the dollar component of the price, and the second being the cents and the sale type.
     */
    fun getBestNonLocalPrice(
        retailers: Map<String, Retailer>,
        region: String
    ): Pair<String, String>? {
        val localRetailers =
            retailers.filter {
                it.value.local != true && it.value.stores?.any { store ->
                    store.region.equals(
                        region,
                        true
                    )
                } == true
            }.keys

        val nonLocalPrices = information!!.filter {
            localRetailers.contains(it.retailer) && it.pricing!!.any { (pricingStore) ->
                retailers[it.retailer]!!.stores!!.first { (id) -> id == pricingStore }.region.equals(
                    region,
                    true
                )
            }
        }

        return findLowestPrice(nonLocalPrices, retailers, region)
    }

    /**
     * Find the lowest prices based on a filtered subset of the products for this product.
     *
     * @param products The products to find the lowest price of.
     * @return A pair, with the dollars component (e.g. "$10" for $10.00/kg) as the first value, and
     * the cents component (for example, ".00/kg" for "$10.00/kg) as the second value.
     */
    private fun findLowestPrice(
        products: List<RetailerProductInformation>,
        retailers: Map<String, Retailer>,
        region: String
    ): Pair<String, String>? {
        if (products.isNotEmpty()) {
            val lowestPricePair =
                products.flatMap { productInfo ->
                    productInfo.pricing!!.filter { (store, _, _, _, _, _, _, _) ->
                        retailers[productInfo.retailer]!!.stores?.first { (id, _, _, _, _, _, _) -> id == store }!!.region.equals(
                            region,
                            true
                        )
                    }
                        .map { it to productInfo }
                }
                    .minBy { it.first.getPrice(it.second) }

            return lowestPricePair.first.getDisplayPrice(lowestPricePair.second)
        }

        return null
    }


    /**
     * Get information which is filtered based on the users selected region.
     *
     * @param region The region (city/town) that the user wants to compare products for.
     * @param retailers Contains information about the retailers are the region they are in.
     * @return Information about what products are in each particular region.
     */
    fun getFilteredInformation(
        region: String,
        retailers: Map<String, Retailer>
    ): List<RetailerProductInformation> {

        val retailersInRegion =
            retailers.filter {
                it.value.stores!!.any { store ->
                    store.region.equals(
                        region,
                        true
                    )
                }
            }.keys

        return information!!.filter { retailersInRegion.contains(it.retailer) }.map { info ->
            info.pricing =
                info.pricing!!.filter {
                    retailers.any { (key, value) ->
                        key == info.retailer && value.stores!!.any { store ->
                            store.id == it.store && store.region.equals(
                                region,
                                true
                            )
                        }
                    }
                }
                    .toMutableList()
            info
        }
    }
}
