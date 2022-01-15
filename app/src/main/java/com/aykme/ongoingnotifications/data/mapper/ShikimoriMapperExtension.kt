package com.aykme.ongoingnotifications.data.mapper

import com.aykme.ongoingnotifications.data.source.remote.shikimoriapi.AnimeResponse
import com.aykme.ongoingnotifications.domain.model.Anime

fun List<AnimeResponse>.responseToEntityList(): List<Anime> {
    return this.map {
        Anime(
            id = it.id,
            name = it.russianName ?: it.englishName,
            image = it.imageResponse?.originalSizeUrl,
            score = it.score,
            episodesAired = it.episodesAired,
            episodes = it.episodes
        )
    }
}