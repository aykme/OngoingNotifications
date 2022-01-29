package com.aykme.animenoti.data.mapper

import com.aykme.animenoti.data.source.remote.shikimoriapi.AnimeDetailsResponse
import com.aykme.animenoti.data.source.remote.shikimoriapi.AnimeResponse
import com.aykme.animenoti.domain.model.Anime
import com.aykme.animenoti.domain.model.AnimeStatus

fun List<AnimeResponse>.toEntityList(): List<Anime> {
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

fun AnimeDetailsResponse.toEntity(): Anime {
    return Anime(
        id = this.id,
        name = this.russianName ?: this.englishName,
        imageUrl = this.imageResponse?.originalSizeUrl,
        score = this.score,
        episodesAired = this.episodesAired,
        episodesTotal = this.episodes,
        airedOn = this.airedOn,
        releasedOn = this.releasedOn,
        status = when (this.status) {
            AnimeStatus.ONGOING.value -> AnimeStatus.ONGOING
            AnimeStatus.ANONS.value -> AnimeStatus.ANONS
            AnimeStatus.RELEASED.value -> AnimeStatus.RELEASED
            else -> AnimeStatus.UNKNOWN
        }
    )
}