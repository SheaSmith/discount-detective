package com.example.cosc345project

import android.app.Application
import com.example.cosc345.shared.models.Product
import com.example.cosc345project.repository.SearchRepository
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@HiltAndroidApp
class DiscountDetectorApp : Application() {
    companion object {
        var applicationScope = MainScope()
    }

    override fun onCreate() {
        super.onCreate()
        val repo = SearchRepository(this@DiscountDetectorApp)
        applicationScope.launch(Dispatchers.IO) {
            repo.initialise()
        }

        getProducts(repo)
    }

    @Suppress("UNUSED_VALUE")
    private fun getProducts(
        repo: SearchRepository,
        firstKey: String? = null,
        previousKey: String? = null
    ) {
        var query =
            Firebase.database("https://discount-detective-default-rtdb.asia-southeast1.firebasedatabase.app/").reference
                .child("products")
                .limitToFirst(2000)
                .orderByKey()

        if (firstKey != null) {
            query = query.startAt(firstKey)
        }

        query.get().addOnSuccessListener {
            applicationScope.launch(Dispatchers.IO) {
                var products: List<Pair<String, Product>>? = it.children.map { Pair(it.key!!, it.getValue<Product>()!!) }


                val newKey = products!!.last().first

                repo.setProducts(products)
                val size = products.size
                    products = null
                    System.gc()
                if (size == 2000) {
                    getProducts(repo, newKey, firstKey)
                }
            }
        }
    }
}