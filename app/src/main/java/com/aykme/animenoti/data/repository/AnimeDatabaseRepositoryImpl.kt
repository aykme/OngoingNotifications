package com.aykme.animenoti.data.repository

import com.aykme.animenoti.data.source.local.animedatabase.AnimeDao
import com.aykme.animenoti.domain.model.Anime
import com.aykme.animenoti.domain.repository.AnimeDatabaseRepository
import kotlinx.coroutines.flow.Flow

class AnimeDatabaseRepositoryImpl(private val animeDao: AnimeDao) : AnimeDatabaseRepository {

    override suspend fun insert(anime: Anime) {
        animeDao.insert(anime)
    }

    override suspend fun update(anime: Anime) {
        animeDao.update(anime)
    }

    override suspend fun getItem(id: Int): Anime {
        return animeDao.getItem(id)
    }

    override fun getItemsAsFlow(): Flow<List<Anime>> {
        return animeDao.getItemsAsFlow()
    }

    override suspend fun getItems(): List<Anime> {
        return animeDao.getItems()
    }

    override suspend fun delete(id: Int) {
        animeDao.delete(id)
    }

    override suspend fun deleteAll() {
        animeDao.deleteAll()
    }

    override suspend fun updateItemsNewEpisodeStatus(hasNewEpisode: Boolean) {
        animeDao.updateItemsNewEpisodeStatus(hasNewEpisode)
    }
}