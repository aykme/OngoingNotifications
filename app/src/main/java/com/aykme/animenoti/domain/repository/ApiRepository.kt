package com.aykme.animenoti.domain.repository

import com.aykme.animenoti.domain.model.Anime

interface ApiRepository {
    suspend fun getOngoings(page: Int, limit: Int): List<Anime>
    suspend fun getAnnounced(page: Int, limit: Int): List<Anime>
    suspend fun getAnimeListByIds(page: Int, limit: Int, ids: String): List<Anime>
    suspend fun getAnimeById(id: Int): Anime
    suspend fun getAnimeListBySearch(page: Int, limit: Int, search: String): List<Anime>
}