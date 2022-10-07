package io.github.sheasmith.discountdetective.extensions

import java.text.DecimalFormat
import java.text.ParseException

/**
 * Convert the double to a string that doesn't always have a decimal.
 *
 * @return The formatted number.
 */
fun Double.toPrettyString(): String {
    val nf = DecimalFormat("##.###")
    return nf.format(this)
}

/**
 * Convert a pretty double string back to a double.
 *
 * @return The double that has been extracted.
 */
fun String.fromPrettyDouble(): Double? {
    val nf = DecimalFormat("##.###")
    return try {
        nf.parse(this)?.toDouble()
    } catch (e: ParseException) {
        null
    }
}