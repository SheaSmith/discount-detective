package com.example.cosc345project

import com.example.cosc345.shared.models.RetailerProductInformation
import com.example.cosc345.shared.models.SaleType
import com.example.cosc345.shared.models.Store

abstract class BaseTests {
    protected fun allProductsHavePrices(products: List<RetailerProductInformation>): Boolean {
        System.out.format("+------------------+--------------------+%n")
        System.out.format("| Number of prices | Number of products |%n")
        System.out.format("+------------------+--------------------+%n")

        val rowFormat = "| %-16d | %-18d |%n"

        products.sortedBy { it.pricing!!.size }.groupBy { it.pricing!!.size }
            .forEach { (price, products) ->
                System.out.format(rowFormat, price, products.size)
            }

        System.out.format("+------------------+--------------------+%n")

        assert(products.none { product -> product.pricing?.any { it.price == null && it.discountPrice == null } == true })

        return products.none { it.pricing!!.isEmpty() }
    }

    protected fun allProductsHaveRequiredFields(products: List<RetailerProductInformation>) {
        assert(products.none { it.retailer.isNullOrEmpty() })
        assert(products.none { it.id.isNullOrEmpty() })
        assert(products.none { it.name.isNullOrEmpty() })
        assert(products.none { it.saleType == null })
        assert(products.none { it.image.isNullOrEmpty() })
        assert(products.none { it.saleType == SaleType.WEIGHT && it.weight == null })
    }

    fun allStoresHaveRequiredFields(stores: List<Store>) {
        assert(stores.none { it.id.isNullOrEmpty() })
        assert(stores.none { it.name.isNullOrEmpty() })
        assert(stores.none { it.automated == null })
    }
}