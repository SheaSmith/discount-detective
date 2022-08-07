package com.example.cosc345project

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.example.cosc345project.repository.SearchRepository
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import javax.inject.Inject

@HiltAndroidApp
class DiscountDetectorApp : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    private val scope = CoroutineScope(newSingleThreadContext("name"))

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this, FirebaseOptions.fromResource(this)!!)

        val searchRepository = SearchRepository(
            this,
            Firebase.database("https://discount-detective-default-rtdb.asia-southeast1.firebasedatabase.app/")
        )

        scope.launch {
            if (!searchRepository.hasIndexed()) {
                searchRepository.indexFromFirebase()
            }
        }
    }
}