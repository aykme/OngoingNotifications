package com.aykme.ongoingnotifications.data.source.remote.shikimoriapi

import com.squareup.moshi.Json

data class AnimeResponse(
    val id: Int,
    @Json(name = "name") val englishName: String?,
    @Json(name = "russian") val russianName: String?,
    @Json(name = "image") val imageResponse: ImageResponse?,
    val url: String?,
    val kind: String?,
    val score: Float?,
    val status: String?,
    val episodes: Int?,
    @Json(name = "episodes_aired") val episodesAired: Int?,
    @Json(name = "aired_on") val airedOn: String?,
    @Json(name = "released_on") val releasedOn: String?
)