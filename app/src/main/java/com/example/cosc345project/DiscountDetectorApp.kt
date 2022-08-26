package com.example.cosc345project

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.cosc345project.workers.IndexingWorker
import dagger.hilt.android.HiltAndroidApp
import java.time.Duration
import javax.inject.Inject

/**
 * DiscountDetectorApp class
 *
 *
 */
@HiltAndroidApp
class DiscountDetectorApp : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        WorkManager.getInstance(this).cancelAllWork()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "indexer",
                ExistingPeriodicWorkPolicy.KEEP,
                PeriodicWorkRequestBuilder<IndexingWorker>(Duration.ofHours(1))
                    .build()
            )
    }
}