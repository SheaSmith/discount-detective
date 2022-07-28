package com.example.cosc345.shared.models

/**
 * An enum which determines how the product is measured for the purposes of determining a price.
 */
enum class SaleType {
    /**
     * The product is sold individually, so the price is determined by how many units are being bought.
     */
    EACH,

    /**
     * The product is sold by weight, so the price is determined by the weight of the product.
     */
    WEIGHT
}