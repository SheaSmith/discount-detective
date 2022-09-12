package com.example.cosc345.scraperapp.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.example.cosc345.scraperapp.R
import com.example.cosc345.scraperapp.repositories.ScraperRepository
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.Duration

/**
 * The background worker responsible for scraping data from retailer websites.
 */
@HiltWorker
class ScraperWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val scraperRepository: ScraperRepository
) : BaseWorker(context, params) {
    override suspend fun doWork(): Result {
        workStart(R.string.notification_scraper)

        try {
            scraperRepository.runScrapers()
        } catch (e: Exception) {
            e.printStackTrace()
            Firebase.crashlytics.recordException(e)
            workStop()
            return Result.retry()
        }

        workStop()

        val workRequest = OneTimeWorkRequestBuilder<BarcodeMergeWorker>()
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                Duration.ofMinutes(30)
            )
            .build()

        WorkManager
            .getInstance(applicationContext)
            .enqueueUniqueWork("barcode", ExistingWorkPolicy.REPLACE, workRequest)

        return Result.success()
    }
}