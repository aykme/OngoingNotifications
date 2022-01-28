package com.aykme.animenoti.domain.usecase

import com.aykme.animenoti.domain.repository.AnimeDatabaseRepository

class DeleteAllDatabaseItemsUseCase(private val repository: AnimeDatabaseRepository) {
    suspend operator fun invoke() {
        repository.deleteAll()
    }
}