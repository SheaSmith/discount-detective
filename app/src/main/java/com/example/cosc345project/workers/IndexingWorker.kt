package com.example.cosc345project.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.cosc345project.repository.SearchRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class IndexingWorker @AssistedInject constructor(
    @Assisted val appContext: Context,
    @Assisted params: WorkerParameters,
    private val searchRepository: SearchRepository
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        searchRepository.indexFromFirebase()

        return Result.success()
    }
}