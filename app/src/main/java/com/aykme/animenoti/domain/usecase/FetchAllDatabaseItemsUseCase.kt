package com.aykme.animenoti.domain.usecase

import com.aykme.animenoti.domain.model.Anime
import com.aykme.animenoti.domain.repository.AnimeDatabaseRepository

class FetchAllDatabaseItemsUseCase(private val repository: AnimeDatabaseRepository) {
    suspend operator fun invoke(): List<Anime> {
        return repository.getItems()
    }
}