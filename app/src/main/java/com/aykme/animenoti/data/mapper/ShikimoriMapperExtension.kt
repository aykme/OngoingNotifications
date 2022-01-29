package com.aykme.animenoti.data.mapper

import com.aykme.animenoti.data.source.remote.shikimoriapi.AnimeResponse
import com.aykme.animenoti.domain.model.Anime
import com.aykme.animenoti.domain.model.AnimeStatus

fun List<AnimeResponse>.responseToEntityList(): List<Anime> {
    return this.map { animeResponse ->

        Anime(
            id = animeResponse.id,
            name = animeResponse.russianName ?: animeResponse.englishName,
            imageUrl = animeResponse.imageResponse?.originalSizeUrl,
            score = animeResponse.score,
            episodesAired = animeResponse.episodesAired,
            episodesTotal = animeResponse.episodes,
            airedOn = animeResponse.airedOn,
            releasedOn = animeResponse.releasedOn,
            status = when (animeResponse.status) {
                AnimeStatus.ONGOING.value -> AnimeStatus.ONGOING
                AnimeStatus.ANONS.value -> AnimeStatus.ANONS
                AnimeStatus.RELEASED.value -> AnimeStatus.RELEASED
                else -> AnimeStatus.UNKNOWN
            }
        )
    }


}