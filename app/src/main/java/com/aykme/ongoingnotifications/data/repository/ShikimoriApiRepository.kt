package com.aykme.ongoingnotifications.data.repository

import com.aykme.ongoingnotifications.data.mapper.responseToEntityList
import com.aykme.ongoingnotifications.data.source.remote.shikimoriapi.AnimeResponse
import com.aykme.ongoingnotifications.data.source.remote.shikimoriapi.ShikimoriApi
import com.aykme.ongoingnotifications.domain.model.Anime
import com.aykme.ongoingnotifications.domain.repository.ApiRepository

class ShikimoriApiRepository(private val api: ShikimoriApi) : ApiRepository {

    override suspend fun getOngoings(page: Int, limit: Int): List<Anime> {
        val animeResponseList: List<AnimeResponse> =
            api.getAnimeList(page, limit, "ongoing", "ranked")
        return animeResponseList.responseToEntityList()
    }
}