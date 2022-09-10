package io.github.sheasmith.discountdetective

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp
import io.github.sheasmith.discountdetective.workers.IndexingWorker
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