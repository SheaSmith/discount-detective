package com.example.cosc345.shared.models

enum class Weight(private val unit: String) {
    GRAMS("g"),
    KILOGRAMS("kg");

    override fun toString(): String {
        return unit
    }
}