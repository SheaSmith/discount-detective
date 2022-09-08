package com.example.cosc345.shared.models

/**
 * Different units, the unit used to denote them, and regex to parse them out.
 *
 * @constructor Specify the parameters for a new unit enum.
 */
enum class Units(
    /**
     * The suffix to use for this unit, for example "g" for grams.
     */
    private val unit: String,

    /**
     * The regex to use to parse out this unit. Ideally it should contain a captureable number.
     */
    val regex: Regex
) {
    /**
     * The weight unit grams, for example 100g.
     */
    GRAMS("g", "(\\d+\\.?\\d*)\\s*gm?s?".toRegex(RegexOption.IGNORE_CASE)),

    /**
     * A range of grams, for example 80-100g.
     */
    GRAMS_RANGE("g", "(\\d+\\.?\\d*)-(\\d+\\.?\\d*)\\s*gm?".toRegex(RegexOption.IGNORE_CASE)),

    /**
     * The weight unit kilograms, for example 1.5kg.
     */
    KILOGRAMS("kg", "(\\d+\\.?\\d*)\\s*kg".toRegex(RegexOption.IGNORE_CASE)),

    /**
     * A range of kilograms, for example 1-1.5kg
     */
    KILOGRAMS_RANGE("g", "(\\d+\\.?\\d*)-(\\d+\\.?\\d*)\\s*kg".toRegex(RegexOption.IGNORE_CASE)),

    /**
     * The liquid volume unit millilitres, for example 100mL.
     */
    MILLILITRES("mL", "(\\d+\\.?\\d*)\\s*ml".toRegex(RegexOption.IGNORE_CASE)),

    /**
     * The liquid volume unit litres, for example 1.5L.
     */
    LITRES("L", "(\\d+\\.?\\d*)\\s*Lt?r?".toRegex(RegexOption.IGNORE_CASE)),

    /**
     * A "pack" of something, for example a 6 pack of cans.
     */
    PACK(" pack", "(\\d+\\.?\\d*)\\s*(?:Pack|pk)".toRegex(RegexOption.IGNORE_CASE)),

    /**
     * The distance unit metres, for example 10m.
     */
    METRES("m", "(\\d+\\.?\\d*)\\s*m".toRegex(RegexOption.IGNORE_CASE));

    /**
     * Return the suffix for the unit.
     */
    override fun toString(): String {
        return unit
    }

    companion object {
        /**
         * An array of all of the possible units, for use when extracting or removing them from text.
         */
        val all: Array<Units>
            get() = arrayOf(
                GRAMS_RANGE,
                GRAMS,
                KILOGRAMS_RANGE,
                KILOGRAMS,
                MILLILITRES,
                LITRES,
                PACK,
                METRES
            )
    }
}