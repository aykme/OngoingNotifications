package com.aykme.animenoti.data.source.remote.shikimoriapi

class AnimeDetailsResponse(
    id: Int,
    englishName: String?,
    russianName: String?,
    imageResponse: ImageResponse?,
    url: String?,
    kind: String?,
    score: Float?,
    status: String?,
    episodes: Int?,
    episodesAired: Int?,
    airedOn: String?,
    releasedOn: String?
) : AnimeResponse(
    id,
    englishName,
    russianName,
    imageResponse,
    url,
    kind,
    score,
    status,
    episodes,
    episodesAired,
    airedOn,
    releasedOn
)