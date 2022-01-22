package com.aykme.animenotifications.domain.usecase

import com.aykme.animenotifications.domain.repository.AnimeDatabaseRepository

class DeleteAllDatabaseItemsUseCase(private val repository: AnimeDatabaseRepository) {
    suspend operator fun invoke() {
        repository.deleteAll()
    }
}