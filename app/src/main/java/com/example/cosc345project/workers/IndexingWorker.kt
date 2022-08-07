package com.example.cosc345project.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import androidx.work.multiprocess.RemoteCoroutineWorker
import com.example.cosc345project.repository.SearchRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class IndexingWorker @AssistedInject constructor(
    @Assisted val appContext: Context,
    @Assisted params: WorkerParameters,
    private val searchRepository: SearchRepository
) : RemoteCoroutineWorker(appContext, params) {

    override suspend fun doRemoteWork(): Result {
        searchRepository.indexFromFirebase()

        return Result.success()
    }
}