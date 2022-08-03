package com.example.cosc345.scraperapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject


@HiltWorker
class ScraperWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val scraperRepository: ScraperRepository
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val wakeLock: PowerManager.WakeLock =
            (getSystemService(applicationContext, PowerManager::class.java) as PowerManager).run {
                newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp::MyWakelockTag").apply {
                    acquire()
                }
            }


        setForeground(createForegroundInfo())
        try {
            val response = scraperRepository.runScrapers()
            scraperRepository.saveScrapers(response.first, response.second)
        } catch (e: Exception) {
            e.printStackTrace()
            Firebase.crashlytics.recordException(e)
            wakeLock.release()
            return Result.retry()
        }


        wakeLock.release()
        return Result.success()
    }

    private fun createForegroundInfo(): ForegroundInfo {
        val id = applicationContext.getString(R.string.notification_channel_id)
        val title = applicationContext.getString(R.string.notification_title)
        val cancel = applicationContext.getString(R.string.cancel)
        val intent = WorkManager.getInstance(applicationContext)
            .createCancelPendingIntent(getId())

        val notificationManager =
            applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            id,
            title,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(applicationContext, id)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle(title)
            .setContentText(applicationContext.getString(R.string.notification_text))
            .setTicker(title)
            .setOngoing(true)
            // Add the cancel action to the notification which can
            // be used to cancel the worker
            .addAction(android.R.drawable.ic_delete, cancel, intent)
            .build()

        return ForegroundInfo(1, notification)
    }
}