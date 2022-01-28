package com.aykme.animenoti.domain.usecase

import com.aykme.animenoti.domain.model.Anime
import com.aykme.animenoti.domain.repository.ApiRepository

class FetchAnnouncedAnimeListUseCase(private val repository: ApiRepository) {
    suspend operator fun invoke(page: Int, limit: Int): List<Anime> =
        repository.getAnnounced(page, limit)
}
