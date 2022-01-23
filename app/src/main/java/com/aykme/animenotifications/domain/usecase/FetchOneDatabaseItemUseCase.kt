package com.aykme.animenotifications.domain.usecase

import com.aykme.animenotifications.domain.model.Anime
import com.aykme.animenotifications.domain.repository.AnimeDatabaseRepository
import kotlinx.coroutines.flow.Flow

class FetchOneDatabaseItemUseCase(private val repository: AnimeDatabaseRepository) {
    operator fun invoke(id: Int): Flow<Anime> {
        return repository.getItem(id)
    }
}