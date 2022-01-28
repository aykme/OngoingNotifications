package com.aykme.animenoti.domain.usecase

import com.aykme.animenoti.domain.model.Anime
import com.aykme.animenoti.domain.repository.AnimeDatabaseRepository
import kotlinx.coroutines.flow.Flow

class FetchAllDatabaseItemsUseCase(private val repository: AnimeDatabaseRepository) {
    operator fun invoke(): Flow<List<Anime>> {
        return repository.getItems()
    }
}