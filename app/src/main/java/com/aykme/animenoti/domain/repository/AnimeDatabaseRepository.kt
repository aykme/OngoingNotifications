package com.aykme.animenoti.domain.repository

import com.aykme.animenoti.domain.model.Anime
import kotlinx.coroutines.flow.Flow

interface AnimeDatabaseRepository {

    suspend fun insert(anime: Anime)

    suspend fun update(anime: Anime)

    suspend fun getItem(id: Int): Anime

    fun getItemsAsFlow(): Flow<List<Anime>>

    suspend fun getItems(): List<Anime>

    suspend fun delete(id: Int)

    suspend fun deleteAll()
}