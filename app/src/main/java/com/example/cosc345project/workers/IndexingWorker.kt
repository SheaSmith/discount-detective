package com.example.cosc345project.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.example.cosc345project.R
import com.example.cosc345project.repository.SearchIndexRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class IndexingWorker @AssistedInject constructor(
    @Assisted val appContext: Context,
    @Assisted params: WorkerParameters,
    private val searchRepository: SearchIndexRepository
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        setForeground(createForegroundInfo())

        searchRepository.indexFromFirebase()

        return Result.success()
    }

    private fun createForegroundInfo(): ForegroundInfo {
        val id = applicationContext.getString(R.string.notification_channel_id)
        val title = applicationContext.getString(R.string.notification_title)

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
            .setColor(applicationContext.getColor(R.color.theme))
            .setContentTitle(title)
            .setContentText(applicationContext.getString(R.string.notification_content))
            .setTicker(title)
            .setOngoing(true)
            .build()

        return ForegroundInfo(1, notification)
    }
}