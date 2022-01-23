package com.aykme.animenotifications.domain.usecase

import com.aykme.animenotifications.domain.model.Anime
import com.aykme.animenotifications.domain.repository.AnimeDatabaseRepository
import kotlinx.coroutines.flow.Flow

class FetchAllDatabaseItemsUseCase(private val repository: AnimeDatabaseRepository) {
    operator fun invoke(): Flow<List<Anime>> {
        return repository.getItems()
    }
}