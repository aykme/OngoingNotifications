package com.aykme.animenoti.domain.usecase

import com.aykme.animenoti.domain.model.Anime
import com.aykme.animenoti.domain.repository.ApiRepository

class FetchAnimeByIdUseCase(private val repository: ApiRepository) {
    suspend operator fun invoke(id: Int): Anime {
        return repository.getAnimeById(id)
    }
}