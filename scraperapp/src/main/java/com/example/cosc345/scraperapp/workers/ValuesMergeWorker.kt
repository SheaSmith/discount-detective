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
 * The worker responsible for merging products based on their names.
 */
@HiltWorker
class ValuesMergeWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val scraperRepository: ScraperRepository
) : BaseWorker(context, params) {
    override suspend fun doWork(): Result {
        workStart(R.string.notification_values)

        try {
            scraperRepository.matchValues()
        } catch (e: Exception) {
            e.printStackTrace()
            Firebase.crashlytics.recordException(e)
            workStop()
            return Result.retry()
        }

        workStop()

        val workRequest = OneTimeWorkRequestBuilder<SaveToFirebaseWorker>()
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                Duration.ofMinutes(30)
            )
            .build()

        WorkManager
            .getInstance(applicationContext)
            .enqueueUniqueWork("firebase", ExistingWorkPolicy.REPLACE, workRequest)

        return Result.success()
    }
}