package com.aykme.animenoti.background.workers

import android.content.Context
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.aykme.animenoti.domain.model.Anime
import com.aykme.animenoti.domain.usecase.FetchAnimeByIdUseCase
import com.aykme.animenoti.domain.usecase.UpdateDatabaseItemUseCase

const val REFRESH_ANIME_DATA_WORK = "RefreshAnimeDataWork"

@WorkerThread
class RefreshAnimeDataWork(
    appContext: Context,
    params: WorkerParameters,
    private val fetchAnimeByIdUseCase: FetchAnimeByIdUseCase,
    private val updateDatabaseItemUseCase: UpdateDatabaseItemUseCase
) :
    CoroutineWorker(appContext, params) {

    private lateinit var databaseItems: List<Anime>

    override suspend fun doWork(): Result {
        Log.d(REFRESH_ANIME_DATA_WORK, "doWork() start")
        return try {
            Log.d(REFRESH_ANIME_DATA_WORK, "Database Items $databaseItems")
            Log.d(REFRESH_ANIME_DATA_WORK, "doWork() end success")
            for (item in databaseItems) {
                val anime = fetchAnimeByIdUseCase(item.id)
                Log.d(REFRESH_ANIME_DATA_WORK, "Remote Item $anime")

            }
            Result.success()
        } catch (e: Throwable) {
            Log.d(REFRESH_ANIME_DATA_WORK, "doWork() failure")
            Result.failure()
        }
    }

    class Factory(
        private val fetchAnimeByIdUseCase: FetchAnimeByIdUseCase,
        private val updateDatabaseItemUseCase: UpdateDatabaseItemUseCase
    ) : WorkerFactory() {
        override fun createWorker(
            appContext: Context,
            workerClassName: String,
            workerParameters: WorkerParameters
        ): ListenableWorker {
            return RefreshAnimeDataWork(
                appContext,
                workerParameters,
                fetchAnimeByIdUseCase,
                updateDatabaseItemUseCase
            )
        }
    }
}