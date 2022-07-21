package com.example.cosc345.shared.extensions

fun String.titleCase() =
    this.split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }

fun String.capitaliseNZ() =
    this.split(" ").joinToString(" ") { if (it == "nz") it.uppercase() else it }