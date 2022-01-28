package com.aykme.animenoti.domain.usecase

import com.aykme.animenoti.domain.model.Anime
import com.aykme.animenoti.domain.repository.ApiRepository

class FetchOngoingAnimeListUseCase(private val repository: ApiRepository) {
    suspend operator fun invoke(page: Int, limit: Int): List<Anime> =
        repository.getOngoings(page, limit)
}