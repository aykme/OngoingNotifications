package com.aykme.animenotifications.data.mapper

import com.aykme.animenotifications.data.source.remote.shikimoriapi.AnimeResponse
import com.aykme.animenotifications.domain.model.Anime

fun List<AnimeResponse>.responseToEntityList(): List<Anime> {
    return this.map { animeResponse ->
        Anime(
            id = animeResponse.id!!,
            name = animeResponse.russianName ?: animeResponse.englishName!!,
            imageUrl = animeResponse.imageResponse?.originalSizeUrl!!,
            score = animeResponse.score!!,
            episodesAired = animeResponse.episodesAired!!,
            episodes = animeResponse.episodes!!
        )
    }
}