package com.aykme.animenoti.background.workers

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.aykme.animenoti.R
import com.aykme.animenoti.background.notification.WorkManagerNotification
import com.aykme.animenoti.data.source.remote.coil.ImageDownloader
import com.aykme.animenoti.data.source.remote.shikimoriapi.BASE_URL
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
            refreshAnimeData(databaseItems)
            Result.success()
        } catch (e: Throwable) {
            Result.failure()
        }
    }

    private suspend fun refreshAnimeData(databaseItems: List<Anime>) {
        var itemNumber = 1
        var newEpisodesCount = 0
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
                val notificationTitle = getNotificationTitle(remoteItem)
                val notificationText = getNotificationText(remoteItem)
                val fullImageUrl = BASE_URL + remoteItem.imageUrl
                val notificationImage = ImageDownloader.fetchBitmap(
                    applicationContext,
                    fullImageUrl
                )
                makeNotification(notificationTitle, notificationText, notificationImage)
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
    }

    private fun isNewEpisode(databaseItem: Anime, remoteItem: Anime): Boolean {
        val currentEpisodesAired = databaseItem.episodesAired ?: 0
        val newEpisodesAired = remoteItem.episodesAired ?: 0
        return newEpisodesAired > currentEpisodesAired
    }

    private fun calculateNewEpisodes(databaseItem: Anime, remoteItem: Anime): Int {
        return (remoteItem.episodesAired ?: 0) - (databaseItem.episodesAired ?: 0)
    }

    private fun getNotificationTitle(remoteItem: Anime): String {
        return remoteItem.name ?: resources.getString(R.string.unknown)
    }

    private fun getNotificationText(remoteItem: Anime): String {
        val newEpisodeNumber = remoteItem.episodesAired ?: 0
        return resources.getString(R.string.work_manager_notification_title, newEpisodeNumber)
    }

    private fun makeNotification(
        contentTitle: String,
        contentText: String,
        notificationImage: Bitmap
    ) {
        workManagerNotification.makeNotification(
            contentTitle,
            contentText,
            notificationImage
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