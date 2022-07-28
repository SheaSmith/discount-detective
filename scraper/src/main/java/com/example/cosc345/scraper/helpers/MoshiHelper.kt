package com.example.cosc345.scraper.helpers

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

/**
 * Get the standard Moshi instance that we use across the project.
 *
 * @author Shea Smith
 */
fun getMoshi(): Moshi {
    return Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
}