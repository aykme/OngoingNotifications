package com.aykme.animenoti.background.workers

import android.content.Context
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.aykme.animenoti.domain.model.Anime
import com.aykme.animenoti.domain.usecase.FetchAllDatabaseItems
import com.aykme.animenoti.domain.usecase.FetchAnimeByIdUseCase
import com.aykme.animenoti.domain.usecase.UpdateDatabaseItemUseCase

const val REFRESH_ANIME_DATA_WORK = "RefreshAnimeDataWork"

@WorkerThread
class RefreshAnimeDataWork(
    appContext: Context,
    params: WorkerParameters,
    private val fetchAllDatabaseItems: FetchAllDatabaseItems,
    private val fetchAnimeByIdUseCase: FetchAnimeByIdUseCase,
    private val updateDatabaseItemUseCase: UpdateDatabaseItemUseCase
) :
    CoroutineWorker(appContext, params) {

    private lateinit var databaseItems: List<Anime>

    override suspend fun doWork(): Result {
        Log.d(REFRESH_ANIME_DATA_WORK, "doWork() start")
        return try {
            databaseItems = fetchAllDatabaseItems()
            Log.d(
                REFRESH_ANIME_DATA_WORK, "databaseItems: $databaseItems" +
                        "\n--------------------------------------------------"
            )
            for (databaseItem in databaseItems) {
                val remoteItem = fetchAnimeByIdUseCase(databaseItem.id)
                Log.d(
                    REFRESH_ANIME_DATA_WORK, "remoteItem: $remoteItem" +
                            "\n--------------------------------------------------"
                )
                val currentEpisodesAired = databaseItem.episodesAired ?: 0
                val newEpisodesAired = remoteItem.episodesAired ?: 0
                if (databaseItem != remoteItem) {
                    updateDatabaseItemUseCase(remoteItem)
                    Log.d(
                        REFRESH_ANIME_DATA_WORK, "Обновлен item: $remoteItem" +
                                "\n--------------------------------------------------"
                    )
                } else {
                    Log.d(REFRESH_ANIME_DATA_WORK, "item не обновлен\n")
                }
            }
            Result.success()
        } catch (e: Throwable) {
            Result.failure()
        }
    }

    class Factory(
        private val fetchAllDatabaseItems: FetchAllDatabaseItems,
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
                fetchAllDatabaseItems,
                fetchAnimeByIdUseCase,
                updateDatabaseItemUseCase
            )
        }
    }
}