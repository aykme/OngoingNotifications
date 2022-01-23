package com.aykme.animenotifications

import android.app.Application
import com.aykme.animenotifications.data.repository.AnimeDatabaseRepositoryImpl
import com.aykme.animenotifications.data.repository.ShikimoriApiRepository
import com.aykme.animenotifications.data.source.local.animedatabase.AnimeRoomDatabase
import com.aykme.animenotifications.data.source.remote.shikimoriapi.ShikimoriApi
import com.aykme.animenotifications.domain.repository.AnimeDatabaseRepository
import com.aykme.animenotifications.domain.repository.ApiRepository

class Application : Application() {
    val apiRepository: ApiRepository by lazy {
        ShikimoriApiRepository(ShikimoriApi.instance)
    }
    private val database: AnimeRoomDatabase by lazy {
        AnimeRoomDatabase.getDatabase(this)
    }
    val databaseRepository: AnimeDatabaseRepository by lazy {
        AnimeDatabaseRepositoryImpl(database.animeDao())
    }
}