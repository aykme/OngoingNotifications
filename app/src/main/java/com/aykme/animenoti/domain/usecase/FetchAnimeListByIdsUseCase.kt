package com.aykme.animenoti.domain.usecase

import com.aykme.animenoti.domain.model.Anime
import com.aykme.animenoti.domain.repository.ApiRepository

class FetchAnimeListByIdsUseCase(private val repository: ApiRepository) {
    suspend operator fun invoke(page: Int, limit: Int, ids: String): List<Anime> =
        repository.getAnimeListByIds(page, limit, ids)
}