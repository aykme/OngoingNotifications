package com.aykme.animenoti.background.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.aykme.animenoti.domain.model.Anime
import com.aykme.animenoti.domain.usecase.FetchAllDatabaseItemsUseCase
import kotlinx.coroutines.flow.collectLatest

const val REFRESH_ANIME_DATA_WORK = "RefreshAnimeDataWork"

class RefreshAnimeDataWork(
    appContext: Context,
    params: WorkerParameters,
    private val fetchAllDatabaseItemsUseCase: FetchAllDatabaseItemsUseCase
) :
    CoroutineWorker(appContext, params) {

    private lateinit var databaseItems: List<Anime>

    override suspend fun doWork(): Result {
        Log.d(REFRESH_ANIME_DATA_WORK, "doWork() start")
        return try {
            fetchAllDatabaseItemsUseCase().collectLatest {
                databaseItems = it
                Log.d(REFRESH_ANIME_DATA_WORK, "DatabaseItems: ${databaseItems.joinToString()}")

            }

            Result.success()
        } catch (e: Throwable) {
            Result.failure()
        }
    }

    class Factory(
        private val fetchAllDatabaseItemsUseCase: FetchAllDatabaseItemsUseCase
    ) : WorkerFactory() {
        override fun createWorker(
            appContext: Context,
            workerClassName: String,
            workerParameters: WorkerParameters
        ): ListenableWorker {
            return RefreshAnimeDataWork(
                appContext,
                workerParameters,
                fetchAllDatabaseItemsUseCase
            )
        }
    }
}