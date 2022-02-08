package com.aykme.animenoti.background.workers

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.aykme.animenoti.MIN_PAGE
import com.aykme.animenoti.PAGE_LIMIT
import com.aykme.animenoti.R
import com.aykme.animenoti.background.notification.WorkManagerNotification
import com.aykme.animenoti.data.source.remote.coil.ImageDownloader
import com.aykme.animenoti.data.source.remote.shikimoriapi.BASE_URL
import com.aykme.animenoti.domain.model.Anime
import com.aykme.animenoti.domain.usecase.FetchAllDatabaseItemsUseCase
import com.aykme.animenoti.domain.usecase.FetchAnimeListByIdsUseCase
import com.aykme.animenoti.domain.usecase.FetchDatabaseItemUseCase
import com.aykme.animenoti.domain.usecase.UpdateDatabaseItemUseCase
import com.aykme.animenoti.ui.base.MainActivity

const val REFRESH_ANIME_DATA_WORK = "RefreshAnimeDataWork"

@WorkerThread
class RefreshAnimeDataWork(
    appContext: Context,
    params: WorkerParameters,
    private val fetchAllDatabaseItemsUseCase: FetchAllDatabaseItemsUseCase,
    private val fetchDatabaseItemUseCase: FetchDatabaseItemUseCase,
    private val updateDatabaseItemUseCase: UpdateDatabaseItemUseCase,
    private val fetchAnimeListByIdsUseCase: FetchAnimeListByIdsUseCase,
    private val workManagerNotification: WorkManagerNotification
) :
    CoroutineWorker(appContext, params) {

    private val resources = applicationContext.resources

    override suspend fun doWork(): Result {
        return try {
            val databaseItems = fetchAllDatabaseItemsUseCase()
            val remoteItems = fetchRemoteItems(databaseItems)
            val pendingIntent = getPendingIntent()
            refreshData(remoteItems, pendingIntent)
            Result.success()
        } catch (e: Throwable) {
            Result.failure()
        }
    }

    private suspend fun fetchRemoteItems(databaseItems: List<Anime>): List<Anime> {
        val remoteItems = mutableListOf<Anime>()
        var databaseIdsBuffer = StringBuffer()
        var itemCount = 0
        for ((index, databaseItem) in databaseItems.withIndex()) {
            databaseIdsBuffer.append("${databaseItem.id},")
            itemCount++
            if (itemCount > (PAGE_LIMIT - 1) || index == databaseItems.size - 1) {
                itemCount = 0
                val databaseIds = databaseIdsBuffer.toString().trim(',')
                databaseIdsBuffer = StringBuffer()
                val tempRemoteItems =
                    fetchAnimeListByIdsUseCase(MIN_PAGE, PAGE_LIMIT, databaseIds)
                remoteItems.addAll(tempRemoteItems)
            }
        }
        return remoteItems.toList()
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun getPendingIntent(): PendingIntent {
        return NavDeepLinkBuilder(applicationContext)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.favorites_anime)
            .createPendingIntent()
    }

    private suspend fun refreshData(remoteItems: List<Anime>, pendingIntent: PendingIntent) {
        var remoteItemNumber = 0
        remoteItems.forEach { remoteItem ->
            remoteItemNumber++
            val id = remoteItem.id
            val databaseItem = fetchDatabaseItemUseCase(id)
            val hasNewEpisode = isNewEpisode(databaseItem, remoteItem)
            if (hasNewEpisode) {
                val notificationTitle = getNotificationTitle(remoteItem)
                val notificationText = getNotificationText(remoteItem)
                val fullImageUrl = BASE_URL + remoteItem.imageUrl
                val notificationImage = ImageDownloader.fetchBitmap(
                    applicationContext,
                    fullImageUrl
                )
                makeNotification(
                    notificationTitle,
                    notificationText,
                    notificationImage,
                    pendingIntent
                )
            }
            if (databaseItem != remoteItem) {
                val updateItem = remoteItem.copy(
                    episodesViewed = databaseItem.episodesViewed,
                    hasNewEpisode = hasNewEpisode
                )
                updateDatabaseItemUseCase(updateItem)
                Log.d(
                    REFRESH_ANIME_DATA_WORK,
                    "item #$remoteItemNumber обновлен ${remoteItem.name}"
                )
            } else {
                Log.d(
                    REFRESH_ANIME_DATA_WORK,
                    "item #$remoteItemNumber не обновлен ${remoteItem.name}"
                )
            }
        }
    }

    private fun isNewEpisode(databaseItem: Anime, remoteItem: Anime): Boolean {
        val currentEpisodesAired = databaseItem.episodesAired ?: 0
        val newEpisodesAired = remoteItem.episodesAired ?: 0
        return newEpisodesAired > currentEpisodesAired
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
        notificationImage: Bitmap,
        pendingIntent: PendingIntent
    ) {
        workManagerNotification.makeNotification(
            contentTitle,
            contentText,
            notificationImage,
            pendingIntent
        )
    }

    class Factory(
        private val fetchAllDatabaseItemsUseCase: FetchAllDatabaseItemsUseCase,
        private val fetchDatabaseItemUseCase: FetchDatabaseItemUseCase,
        private val updateDatabaseItemUseCase: UpdateDatabaseItemUseCase,
        private val fetchAnimeListByIdsUseCase: FetchAnimeListByIdsUseCase,
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
                fetchAllDatabaseItemsUseCase,
                fetchDatabaseItemUseCase,
                updateDatabaseItemUseCase,
                fetchAnimeListByIdsUseCase,
                workManagerNotification
            )
        }
    }
}