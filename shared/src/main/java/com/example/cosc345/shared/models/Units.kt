package com.example.cosc345.shared.models

enum class Units(private val unit: String, val regex: Regex) {
    GRAMS("g", "(\\d+\\.?\\d*)\\s*gm?".toRegex(RegexOption.IGNORE_CASE)),
    GRAMS_RANGE("g", "(\\d+\\.?\\d*)-(\\d+\\.?\\d*)\\s*gm?".toRegex(RegexOption.IGNORE_CASE)),
    KILOGRAMS("kg", "(\\d+\\.?\\d*)\\s*kg".toRegex(RegexOption.IGNORE_CASE)),
    MILLILITRES("mL", "(\\d+\\.?\\d*)\\s*ml".toRegex(RegexOption.IGNORE_CASE)),
    LITRES("L", "(\\d+\\.?\\d*)\\s*L".toRegex(RegexOption.IGNORE_CASE)),
    PACK(" pack", "(\\d+\\.?\\d*)\\s*Pack".toRegex(RegexOption.IGNORE_CASE)),
    METRES("m", "(\\d+\\.?\\d*)\\s*m".toRegex(RegexOption.IGNORE_CASE));

    override fun toString(): String {
        return unit
    }
}