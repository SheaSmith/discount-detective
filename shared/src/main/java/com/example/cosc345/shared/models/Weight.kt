package com.example.cosc345.shared.models

enum class Weight(private val unit: String, val regex: Regex) {
    GRAMS("g", "(\\d+\\.?\\d*)\\s*gm?".toRegex(RegexOption.IGNORE_CASE)),
    KILOGRAMS("kg", "(\\d+\\.?\\d*)\\s*kg".toRegex(RegexOption.IGNORE_CASE));

    override fun toString(): String {
        return unit
    }
}