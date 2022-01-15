package com.aykme.ongoingnotifications.domain.model

data class Anime(
    val id: Int,
    val name: String,
    val image: String,
    val score: Float,
    val episodesAired: Int,
    val episodes: Int
)