package com.aykme.animenoti.domain.usecase

import com.aykme.animenoti.domain.model.Anime
import com.aykme.animenoti.domain.repository.AnimeDatabaseRepository

class InsertDatabaseItemUseCase(private val repository: AnimeDatabaseRepository) {
    suspend operator fun invoke(anime: Anime) {
        repository.insert(anime)
    }
}