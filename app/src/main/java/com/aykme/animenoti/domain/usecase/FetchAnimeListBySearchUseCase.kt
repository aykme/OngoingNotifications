package com.aykme.animenoti.domain.usecase

import com.aykme.animenoti.domain.model.Anime
import com.aykme.animenoti.domain.repository.ApiRepository

class FetchAnimeListBySearchUseCase(private val repository: ApiRepository) {
    suspend operator fun invoke(page: Int, limit: Int, search: String): List<Anime> =
        repository.getAnimeListBySearch(page, limit, search)
}