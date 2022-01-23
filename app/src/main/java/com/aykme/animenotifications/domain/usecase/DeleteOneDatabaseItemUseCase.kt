package com.aykme.animenotifications.domain.usecase

import com.aykme.animenotifications.domain.repository.AnimeDatabaseRepository

class DeleteOneDatabaseItemUseCase(private val repository: AnimeDatabaseRepository) {
    suspend operator fun invoke(id: Int) {
        repository.delete(id)
    }
}