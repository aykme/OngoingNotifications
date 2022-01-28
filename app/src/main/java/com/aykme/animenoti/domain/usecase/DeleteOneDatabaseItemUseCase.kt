package com.aykme.animenoti.domain.usecase

import com.aykme.animenoti.domain.repository.AnimeDatabaseRepository

class DeleteOneDatabaseItemUseCase(private val repository: AnimeDatabaseRepository) {
    suspend operator fun invoke(id: Int) {
        repository.delete(id)
    }
}