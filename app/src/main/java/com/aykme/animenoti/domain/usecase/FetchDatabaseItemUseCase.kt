package com.aykme.animenoti.domain.usecase

import com.aykme.animenoti.domain.model.Anime
import com.aykme.animenoti.domain.repository.AnimeDatabaseRepository

class FetchDatabaseItemUseCase(private val repository: AnimeDatabaseRepository) {
    suspend operator fun invoke(id: Int): Anime {
        return repository.getItem(id)
    }
}