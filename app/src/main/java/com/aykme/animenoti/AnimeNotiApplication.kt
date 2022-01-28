package com.aykme.animenoti

import android.app.Application
import com.aykme.animenoti.data.repository.AnimeDatabaseRepositoryImpl
import com.aykme.animenoti.data.repository.ShikimoriApiRepository
import com.aykme.animenoti.data.source.local.animedatabase.AnimeRoomDatabase
import com.aykme.animenoti.data.source.remote.shikimoriapi.ShikimoriApi
import com.aykme.animenoti.domain.repository.AnimeDatabaseRepository
import com.aykme.animenoti.domain.repository.ApiRepository

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
}