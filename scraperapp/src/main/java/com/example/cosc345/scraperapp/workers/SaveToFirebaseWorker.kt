package com.example.cosc345.scraperapp.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import com.example.cosc345.scraperapp.R
import com.example.cosc345.scraperapp.repositories.ScraperRepository
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * The background worker responsible for saving product information to Firebase.
 */
@HiltWorker
class SaveToFirebaseWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val scraperRepository: ScraperRepository
) : BaseWorker(context, params) {
    override suspend fun doWork(): Result {
        workStart(R.string.notification_firebase)

        try {
            scraperRepository.saveToFirebase()
        } catch (e: Exception) {
            e.printStackTrace()
            Firebase.crashlytics.recordException(e)
            workStop()
            return Result.retry()
        }

        workStop()

        return Result.success()
    }
}