package com.aykme.animenoti.background.workers

import android.content.Context
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.aykme.animenoti.R
import com.aykme.animenoti.background.notification.WorkManagerNotification
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
    private val updateDatabaseItemUseCase: UpdateDatabaseItemUseCase,
    private val workManagerNotification: WorkManagerNotification
) :
    CoroutineWorker(appContext, params) {

    private val resources = applicationContext.resources
    private lateinit var databaseItems: List<Anime>

    override suspend fun doWork(): Result {
        Log.d(REFRESH_ANIME_DATA_WORK, "doWork() start")
        return try {
            databaseItems = fetchAllDatabaseItems()
            Log.d(REFRESH_ANIME_DATA_WORK, "databaseItemsSize: ${databaseItems.size}")
            Log.d(REFRESH_ANIME_DATA_WORK, "databaseItems: ${databaseItems.joinToString()}")
            checkNewEpisodes(databaseItems)
            Result.success()
        } catch (e: Throwable) {
            Result.failure()
        }
    }

    private suspend fun checkNewEpisodes(databaseItems: List<Anime>) {
        var itemNumber = 1
        var newEpisodesCount = 0
        val notificationTextBuffer = StringBuffer()
        for (databaseItem in databaseItems) {
            val remoteItem = try {
                fetchAnimeByIdUseCase(databaseItem.id)
            } catch (e: Throwable) {
                Log.d(REFRESH_ANIME_DATA_WORK, "RemoteItem is Null")
                null
            } ?: continue
            Log.d(REFRESH_ANIME_DATA_WORK, "remoteItem #$itemNumber: ${remoteItem.id}")
            itemNumber++
            val hasNewEpisode = isNewEpisode(databaseItem, remoteItem)
            if (hasNewEpisode) {
                newEpisodesCount += calculateNewEpisodes(databaseItem, remoteItem)
                Log.d(REFRESH_ANIME_DATA_WORK, "newEpisodesCount: $newEpisodesCount")
                notificationTextBuffer.append(", ${databaseItem.name}")
            }
            if (databaseItem != remoteItem) {
                updateDatabaseItemUseCase(remoteItem)
                Log.d(REFRESH_ANIME_DATA_WORK, "Обновлен item: ${remoteItem.id}")
            } else {
                Log.d(REFRESH_ANIME_DATA_WORK, "item не обновлен\n")
            }
            Log.d(
                REFRESH_ANIME_DATA_WORK, "\n" +
                        "--------------------------------------------------\""
            )
        }
        val notificationTitle = getNotificationTitle(newEpisodesCount)
        val notificationText = getNotificationText(notificationTextBuffer)
        makeNotification(
            notificationTitle,
            notificationText
        )
    }

    private fun isNewEpisode(databaseItem: Anime, remoteItem: Anime): Boolean {
        val currentEpisodesAired = databaseItem.episodesAired ?: 0
        val newEpisodesAired = remoteItem.episodesAired ?: 0
        return newEpisodesAired > currentEpisodesAired
    }

    private fun calculateNewEpisodes(databaseItem: Anime, remoteItem: Anime): Int {
        return (remoteItem.episodesAired ?: 0) - (databaseItem.episodesAired ?: 0)
    }

    private fun getNotificationTitle(newEpisodesCount: Int): String {
        return resources.getString(R.string.work_manager_notification_title, newEpisodesCount)
    }

    private fun getNotificationText(notificationTextBuffer: StringBuffer): String {
        return notificationTextBuffer.toString().trim(',', ' ')
    }

    private fun makeNotification(contentTitle: String, contentText: String) {
        workManagerNotification.makeNotification(
            contentTitle,
            contentText
        )
    }

    class Factory(
        private val fetchAllDatabaseItems: FetchAllDatabaseItems,
        private val fetchAnimeByIdUseCase: FetchAnimeByIdUseCase,
        private val updateDatabaseItemUseCase: UpdateDatabaseItemUseCase,
        private val workManagerNotification: WorkManagerNotification
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
                updateDatabaseItemUseCase,
                workManagerNotification
            )
        }
    }
}