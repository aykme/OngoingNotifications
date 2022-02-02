package com.aykme.animenoti.data.repository

import android.accounts.NetworkErrorException
import android.util.Log
import com.aykme.animenoti.data.mapper.toEntity
import com.aykme.animenoti.data.mapper.toEntityList
import com.aykme.animenoti.data.source.remote.shikimoriapi.AnimeDetailsResponse
import com.aykme.animenoti.data.source.remote.shikimoriapi.AnimeResponse
import com.aykme.animenoti.data.source.remote.shikimoriapi.ShikimoriApi
import com.aykme.animenoti.domain.model.Anime
import com.aykme.animenoti.domain.repository.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ShikimoriApiRepository(private val api: ShikimoriApi) : ApiRepository {
    private val tag = "ShikimoriApiRepository"

    override suspend fun getOngoings(page: Int, limit: Int): List<Anime> {
        val animeResponseList: List<AnimeResponse> =
            safeApiCall { api.getAnimeList(page, limit, "ongoing", "ranked") }
        return animeResponseList.toEntityList()
    }

    override suspend fun getAnnounced(page: Int, limit: Int): List<Anime> {
        val animeResponseList: List<AnimeResponse> =
            safeApiCall { api.getAnimeList(page, limit, "anons", "popularity") }
        return animeResponseList.toEntityList()
    }

    override suspend fun getAnimeById(id: Int): Anime {
        val anime: AnimeDetailsResponse = safeApiCall { api.getAnimeById(id) }
        return anime.toEntity()
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun <T> safeApiCall(apiCall: suspend () -> T): T {
        return withContext(Dispatchers.IO) {
            try {
                Thread.sleep(200)
                apiCall.invoke()
            } catch (e: Throwable) {
                print(e.stackTrace)
                Log.d(tag, "Api failure, trying again")
                Thread.sleep(2000)
                try {
                    apiCall.invoke()
                } catch (e: Throwable) {
                    Log.d(tag, "Api failure")
                    throw NetworkErrorException("Api failure")
                }
            }
        }
    }
}