package com.aykme.animenotifications.domain.usecase

import com.aykme.animenotifications.domain.model.Anime
import com.aykme.animenotifications.domain.repository.AnimeDatabaseRepository

class InsertDatabaseItemUseCase(private val repository: AnimeDatabaseRepository) {
    suspend operator fun invoke(anime: Anime) {
        repository.insert(anime)
    }
}