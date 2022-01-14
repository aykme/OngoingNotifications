package com.aykme.ongoingnotifications.data.source.remote.api.shikimori

import com.squareup.moshi.Json

data class ImageResponse(
    @Json(name = "original") val originalSizeUrl: String?,
    @Json(name = "preview") val previewSizeUrl: String?,
    @Json(name = "x96") val x96SizeUrl: String?,
    @Json(name = "x48") val x48SizeUrl: String?
)