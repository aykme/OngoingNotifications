package com.aykme.animenoti.data.repository

import com.aykme.animenoti.data.mapper.toEntity
import com.aykme.animenoti.data.mapper.toEntityList
import com.aykme.animenoti.data.source.remote.shikimoriapi.AnimeResponse
import com.aykme.animenoti.data.source.remote.shikimoriapi.ShikimoriApi
import com.aykme.animenoti.domain.model.Anime
import com.aykme.animenoti.domain.repository.ApiRepository

class ShikimoriApiRepository(private val api: ShikimoriApi) : ApiRepository {

    override suspend fun getOngoings(page: Int, limit: Int): List<Anime> {
        val animeResponseList: List<AnimeResponse> =
            api.getAnimeList(page, limit, "ongoing", "ranked")
        return animeResponseList.toEntityList()
    }

    override suspend fun getAnnounced(page: Int, limit: Int): List<Anime> {
        val animeResponseList: List<AnimeResponse> =
            api.getAnimeList(page, limit, "anons", "popularity")
        return animeResponseList.toEntityList()
    }

    override suspend fun getAnimeById(id: Int): Anime {
        val anime = api.getAnimeById(id)
        return anime.toEntity()
    }
}