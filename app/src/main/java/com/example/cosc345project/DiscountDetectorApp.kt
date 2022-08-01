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
                .orderByKey()

        if (firstKey != null) {
            query = query.startAt(firstKey)
        }

        query = query.limitToFirst(2000)

        query.get().addOnSuccessListener {
            applicationScope.launch(Dispatchers.IO) {
                var products: HashMap<String, Product>? = it.getValue<HashMap<String, Product>>()!!

                val newKey = products!!.keys.last()

                if (newKey != previousKey) {
                    repo.setProducts(products)
                    products = null
                    System.gc()
                    getProducts(repo, newKey, firstKey)
                } else {
                    repo.isFinished.value = true
                }
            }
        }
    }
}