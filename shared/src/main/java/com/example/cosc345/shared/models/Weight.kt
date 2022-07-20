package com.example.cosc345.shared.models

enum class Weight(private val unit: String, val regex: Regex) {
    GRAMS("g", "([\\d+.])[\\sgm]+".toRegex(RegexOption.IGNORE_CASE)),
    KILOGRAMS("kg", "([\\d.]+)\\s*kg".toRegex(RegexOption.IGNORE_CASE));

    override fun toString(): String {
        return unit
    }
}