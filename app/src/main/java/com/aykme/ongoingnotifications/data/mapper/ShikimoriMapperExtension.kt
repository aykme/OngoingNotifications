package com.aykme.ongoingnotifications.data.mapper

import com.aykme.ongoingnotifications.data.source.remote.shikimoriapi.AnimeResponse
import com.aykme.ongoingnotifications.domain.model.Anime

fun List<AnimeResponse>.responseToEntityList(): List<Anime> {
    return this.map { animeResponse ->
        Anime(
            id = animeResponse.id!!,
            name = animeResponse.russianName ?: animeResponse.englishName!!,
            image = animeResponse.imageResponse?.originalSizeUrl!!,
            score = animeResponse.score!!,
            episodesAired = animeResponse.episodesAired!!,
            episodes = animeResponse.episodes!!
        )
    }
}