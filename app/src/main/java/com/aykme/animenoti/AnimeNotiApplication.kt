package com.aykme.animenoti

import android.app.*
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.work.*
import com.aykme.animenoti.background.notification.WorkManagerNotification
import com.aykme.animenoti.background.workers.REFRESH_ANIME_DATA_WORK
import com.aykme.animenoti.background.workers.RefreshAnimeDataWork
import com.aykme.animenoti.data.repository.AnimeDatabaseRepositoryImpl
import com.aykme.animenoti.data.repository.ShikimoriApiRepository
import com.aykme.animenoti.data.source.local.animedatabase.AnimeRoomDatabase
import com.aykme.animenoti.data.source.remote.shikimoriapi.ShikimoriApi
import com.aykme.animenoti.domain.repository.AnimeDatabaseRepository
import com.aykme.animenoti.domain.repository.ApiRepository
import com.aykme.animenoti.domain.usecase.*
import java.util.concurrent.TimeUnit

const val NOTIFICATION_CHANNEL_ID = "Work Manager Notification Channel Id"

class AnimeNotiApplication : Application() {
    val apiRepository: ApiRepository by lazy {
        ShikimoriApiRepository(ShikimoriApi.instance)
    }
    private val database: AnimeRoomDatabase by lazy {
        AnimeRoomDatabase.getDatabase(this)
    }
    val databaseRepository: AnimeDatabaseRepository by lazy {
        AnimeDatabaseRepositoryImpl(database.animeDao())
    }

    override fun onCreate() {
        super.onCreate()
        setupWorkManagerWork()
        setupNotificationManager()
    }

    private fun setupWorkManagerWork() {
        Log.d(REFRESH_ANIME_DATA_WORK, "setupWorkManager() start")
        val workManagerConfiguration = Configuration.Builder()
            .setWorkerFactory(
                RefreshAnimeDataWork.Factory(
                    FetchAllDatabaseItemsUseCase(databaseRepository),
                    FetchDatabaseItemUseCase(databaseRepository),
                    UpdateDatabaseItemUseCase(databaseRepository),
                    FetchAnimeListByIdsUseCase(apiRepository),
                    WorkManagerNotification(this)
                )
            )
            .build()
        WorkManager.initialize(this, workManagerConfiguration)
        val work = PeriodicWorkRequestBuilder<RefreshAnimeDataWork>(15, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                RefreshAnimeDataWork::class.java.name,
                ExistingPeriodicWorkPolicy.KEEP,
                work
            )
        Log.d(REFRESH_ANIME_DATA_WORK, "setupWorkManager() end")
    }

    private fun setupNotificationManager() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannelName = getString(R.string.work_manager_notification_channel_name)
            val description = getString(R.string.work_manager_notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                notificationChannelName,
                importance
            )
            channel.description = description
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}