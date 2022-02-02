package com.aykme.animenoti.data.source.remote.shikimoriapi

import com.squareup.moshi.Json

class VideoResponse(
    @Json(name = "id") val id: Int,
    @Json(name = "url") val url: String?,
    @Json(name = "player_url") val playerUrl: String?
)