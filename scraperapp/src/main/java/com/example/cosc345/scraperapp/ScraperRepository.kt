package com.example.cosc345.scraperapp

import com.example.cosc345.scraper.Matcher
import com.example.cosc345.shared.models.Product
import com.example.cosc345.shared.models.Retailer
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScraperRepository @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase
) {
    suspend fun runScrapers(): Pair<Map<String, Retailer>, Map<String, Product>> {
        return Matcher().run()
    }

    fun saveScrapers(retailers: Map<String, Retailer>, products: Map<String, Product>) {
        firebaseDatabase.getReference("retailers").setValue(retailers)
        firebaseDatabase.getReference("products").setValue(products)
    }
}