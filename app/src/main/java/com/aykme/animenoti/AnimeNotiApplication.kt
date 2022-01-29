package com.aykme.animenoti

import android.app.Application
import android.util.Log
import androidx.work.*
import com.aykme.animenoti.background.workers.REFRESH_ANIME_DATA_WORK
import com.aykme.animenoti.background.workers.RefreshAnimeDataWork
import com.aykme.animenoti.data.repository.AnimeDatabaseRepositoryImpl
import com.aykme.animenoti.data.repository.ShikimoriApiRepository
import com.aykme.animenoti.data.source.local.animedatabase.AnimeRoomDatabase
import com.aykme.animenoti.data.source.remote.shikimoriapi.ShikimoriApi
import com.aykme.animenoti.domain.repository.AnimeDatabaseRepository
import com.aykme.animenoti.domain.repository.ApiRepository
import com.aykme.animenoti.domain.usecase.FetchAllDatabaseItemsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import java.util.concurrent.TimeUnit

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
    private val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        setupWorkManagerWork()
    }

    private fun setupWorkManagerWork() {
        Log.d(REFRESH_ANIME_DATA_WORK, "setupWorkManager() start")
        val workManagerConfiguration = Configuration.Builder()
            .setWorkerFactory(
                RefreshAnimeDataWork.Factory(FetchAllDatabaseItemsUseCase(databaseRepository))
            )
            .build()
        WorkManager.initialize(this, workManagerConfiguration)
        val work = PeriodicWorkRequestBuilder<RefreshAnimeDataWork>(10, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                RefreshAnimeDataWork::class.java.name,
                ExistingPeriodicWorkPolicy.KEEP,
                work
            )
        Log.d(REFRESH_ANIME_DATA_WORK, "setupWorkManager() end")
    }
}