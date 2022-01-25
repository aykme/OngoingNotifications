package com.aykme.animenotifications.domain.repository

import com.aykme.animenotifications.domain.model.Anime

interface ApiRepository {
    suspend fun getOngoings(page: Int, limit: Int): List<Anime>
    suspend fun getAnnounced(page: Int, limit: Int): List<Anime>
}