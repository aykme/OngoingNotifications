package com.aykme.animenoti.data.source.remote.shikimoriapi

import com.squareup.moshi.Json

class AnimeDetailsResponse(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val englishName: String?,
    @Json(name = "russian") val russianName: String?,
    @Json(name = "image") val imageResponse: ImageResponse?,
    @Json(name = "url")val url: String?,
    @Json(name = "kind")val kind: String?,
    @Json(name = "score")val score: Float?,
    @Json(name = "status")val status: String?,
    @Json(name = "episodes")val episodes: Int?,
    @Json(name = "episodes_aired") val episodesAired: Int?,
    @Json(name = "aired_on") val airedOn: String?,
    @Json(name = "released_on") val releasedOn: String?
)