package com.aykme.animenotifications.data.mapper

import com.aykme.animenotifications.data.source.remote.shikimoriapi.AnimeResponse
import com.aykme.animenotifications.domain.model.Anime
import com.aykme.animenotifications.domain.model.AnimeStatus

fun List<AnimeResponse>.responseToEntityList(): List<Anime> {
    return this.map { animeResponse ->

        Anime(
            id = animeResponse.id!!,
            name = animeResponse.russianName ?: animeResponse.englishName!!,
            imageUrl = animeResponse.imageResponse?.originalSizeUrl!!,
            score = animeResponse.score!!,
            episodesAired = animeResponse.episodesAired!!,
            episodesTotal = animeResponse.episodes!!,
            status = when (animeResponse.status) {
                AnimeStatus.ONGOING.value -> AnimeStatus.ONGOING
                AnimeStatus.ANONS.value -> AnimeStatus.ANONS
                AnimeStatus.RELEASED.value -> AnimeStatus.RELEASED
                else -> throw IllegalArgumentException("Unknown AnimeStatus")
            }
        )
    }


}