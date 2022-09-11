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
 * The app and any functions which are global to the app.
 */
@HiltAndroidApp
class DiscountDetectorApp : Application(), Configuration.Provider {
    /**
     * Get the work factory for manual AndroidX worker setup with Hilt.
     */
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    /**
     * Get the AndroidX work configuration for working with hilt.
     */
    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    /**
     * Create the app.
     */
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