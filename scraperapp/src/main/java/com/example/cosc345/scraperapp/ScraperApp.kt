package com.example.cosc345.scraperapp

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * The scraper app class.
 */
@HiltAndroidApp
class ScraperApp : Application(), Configuration.Provider {
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
}