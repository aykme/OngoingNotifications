package com.aykme.ongoingnotifications.domain.usecase

import com.aykme.ongoingnotifications.domain.model.Anime
import com.aykme.ongoingnotifications.domain.repository.ApiRepository

class FetchOngoingAnimeListUseCase(private val repository: ApiRepository) {
    suspend operator fun invoke(page: Int, limit: Int): List<Anime> =
        repository.getOngoings(page, limit)
}