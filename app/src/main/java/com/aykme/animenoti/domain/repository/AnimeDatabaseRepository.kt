package com.aykme.animenoti.domain.repository

import com.aykme.animenoti.domain.model.Anime
import kotlinx.coroutines.flow.Flow

interface AnimeDatabaseRepository {

    suspend fun insert(anime: Anime)

    suspend fun update(anime: Anime)

    fun getItem(id: Int): Flow<Anime>

    fun getItems(): Flow<List<Anime>>

    suspend fun delete(id: Int)

    suspend fun deleteAll()
}