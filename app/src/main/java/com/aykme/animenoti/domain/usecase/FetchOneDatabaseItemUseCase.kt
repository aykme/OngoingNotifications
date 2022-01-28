package com.aykme.animenoti.domain.usecase

import com.aykme.animenoti.domain.model.Anime
import com.aykme.animenoti.domain.repository.AnimeDatabaseRepository
import kotlinx.coroutines.flow.Flow

class FetchOneDatabaseItemUseCase(private val repository: AnimeDatabaseRepository) {
    operator fun invoke(id: Int): Flow<Anime> {
        return repository.getItem(id)
    }
}