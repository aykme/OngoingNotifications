package com.aykme.animenoti.domain.usecase

import com.aykme.animenoti.domain.repository.AnimeDatabaseRepository

class UpdateDatabaseItemsNewEpisodeStatus(private val repository: AnimeDatabaseRepository) {
    suspend operator fun invoke(hasNewEpisode: Boolean) {
        repository.updateItemsNewEpisodeStatus(hasNewEpisode)
    }
}