package com.example.cosc345project.extensions

import android.util.Log
import com.example.cosc345.shared.models.Product
import com.example.cosc345.shared.models.RetailerProductInformation
import com.example.cosc345.shared.models.SaleType
import com.example.cosc345.shared.models.StorePricingInformation
import com.example.cosc345project.models.SearchablePricingInformation
import com.example.cosc345project.models.SearchableProduct
import com.example.cosc345project.models.SearchableRetailerProductInformation
import kotlin.math.roundToInt

fun Product.getBestInformation(): RetailerProductInformation {
    return information!!.sortedWith(
        Comparator { t: RetailerProductInformation, t2: RetailerProductInformation ->
            return@Comparator t.getRetailerScore() - t2.getRetailerScore()
        }
    ).first()
}

fun SearchableProduct.getBestInformation(): SearchableRetailerProductInformation {
    if (information == null) {
        Log.d("HElP", this.id)
    }

    return information!!.sortedWith(
        Comparator { t: SearchableRetailerProductInformation, t2: SearchableRetailerProductInformation ->
            return@Comparator t.getRetailerScore() - t2.getRetailerScore()
        }
    ).first()
}

fun RetailerProductInformation.getRetailerScore(): Int {
    return getScoreForRetailer(this.retailer!!)
}

fun SearchableRetailerProductInformation.getRetailerScore(): Int {
    return getScoreForRetailer(this.retailer)
}

private fun getScoreForRetailer(retailer: String): Int {
    return when (retailer) {
        "countdown" -> 0
        "new-world" -> 1
        "paknsave" -> 1
        "freshchoice" -> 2
        "supervalue" -> 2
        "warehouse" -> 2
        "four-square" -> 4
        else -> 3
    }
}

fun SearchablePricingInformation.getPrice(productInformation: SearchableRetailerProductInformation): Int {
    var price =
        if (discountPrice == null || price?.let { it < discountPrice } == true) {
            price
        } else {
            discountPrice
        }

    if (productInformation.saleType == SaleType.WEIGHT) {
        price = (price!! / (productInformation.weight!!.toDouble() / 1000)).roundToInt()
    }

    return price!!
}

fun StorePricingInformation.getPrice(productInformation: RetailerProductInformation): Int {
    var price =
        if (discountPrice == null || price?.let { it < discountPrice!! } == true) {
            price
        } else {
            discountPrice
        }

    if (productInformation.saleType == SaleType.WEIGHT) {
        price = (price!! / (productInformation.weight!!.toDouble() / 1000)).roundToInt()
    }

    return price!!
}