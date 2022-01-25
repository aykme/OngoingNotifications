package com.aykme.animenotifications.domain.usecase

import com.aykme.animenotifications.domain.model.Anime
import com.aykme.animenotifications.domain.repository.ApiRepository

class FetchAnnouncedAnimeListUseCase(private val repository: ApiRepository) {
    suspend operator fun invoke(page: Int, limit: Int): List<Anime> =
        repository.getAnnounced(page, limit)
}
