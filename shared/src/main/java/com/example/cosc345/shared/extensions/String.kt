package com.example.cosc345.shared.extensions

/**
 * Convert the given string to the title case. For example, This Is Title Case.
 *
 * @author Shea Smith
 */
fun String.titleCase() =
    this.split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }

/**
 * Specific capitalise any instances of "NZ".
 *
 * @author Shea Smith
 */
fun String.capitaliseNZ() =
    this.split(" ").joinToString(" ") { if (it.equals("nz", true)) it.uppercase() else it }