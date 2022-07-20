package com.example.cosc345.shared.extensions

fun String.titleCase() =
    this.split(" ").map { it.replaceFirstChar { char -> char.uppercase() } }.joinToString(" ")