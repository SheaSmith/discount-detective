package com.example.cosc345.scraperapp.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.PowerManager
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.cosc345.scraperapp.R

/**
 * The base worker for the scraper workers.
 */
abstract class BaseWorker(appContext: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(appContext, workerParameters) {
    private lateinit var wakeLock: PowerManager.WakeLock

    /**
     * Called when the worker starts. This sets up the wake lock and notification.
     */
    protected suspend fun workStart(@StringRes text: Int) {
        wakeLock =
            (ContextCompat.getSystemService(
                applicationContext,
                PowerManager::class.java
            ) as PowerManager).run {
                newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp::MyWakelockTag").apply {
                    acquire()
                }
            }


        setForeground(createForegroundInfo(text))
    }

    /**
     * Called when the worker stops. This releases the wakelock.
     */
    protected fun workStop() {
        wakeLock.release()
    }

    private fun createForegroundInfo(@StringRes text: Int): ForegroundInfo {
        val id = applicationContext.getString(R.string.notification_channel_id)
        val title = applicationContext.getString(R.string.notification_title)
        val cancel = applicationContext.getString(R.string.cancel)
        val intent = WorkManager.getInstance(applicationContext)
            .createCancelPendingIntent(getId())

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            id,
            title,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(applicationContext, id)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle(title)
            .setContentText(applicationContext.getString(text))
            .setTicker(title)
            .setOngoing(true)
            // Add the cancel action to the notification which can
            // be used to cancel the worker
            .addAction(android.R.drawable.ic_delete, cancel, intent)
            .build()

        return ForegroundInfo(1, notification)
    }
}