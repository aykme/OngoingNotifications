package com.aykme.ongoingnotifications.domain.repository

import com.aykme.ongoingnotifications.domain.model.Anime

interface ApiRepository {
    suspend fun getOngoings(page: Int, limit: Int): List<Anime>
}