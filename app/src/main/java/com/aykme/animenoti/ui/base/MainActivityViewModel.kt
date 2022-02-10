package com.aykme.animenoti.ui.base

import androidx.lifecycle.*
import com.aykme.animenoti.AnimeNotiApplication
import com.aykme.animenoti.domain.model.Anime
import com.aykme.animenoti.domain.usecase.*
import com.google.android.material.badge.BadgeDrawable
import kotlinx.coroutines.launch

class MainActivityViewModel(
    fetchAllDatabaseItemsAsFlowUseCase: FetchAllDatabaseItemsAsFlowUseCase
) : ViewModel() {

    val followedAnimeList: LiveData<List<Anime>> by lazy {
        fetchAllDatabaseItemsAsFlowUseCase().asLiveData()
    }

    fun bindBadge(badge: BadgeDrawable, followedAnimeList: List<Anime>) {
        viewModelScope.launch {
            val statusCount = calculateNewEpisodeStatusNumber(
                followedAnimeList
            )
            if (statusCount > 0) {
                badge.isVisible = true
                badge.number = statusCount
            } else {
                badge.isVisible = false
            }
        }

    }

    private suspend fun calculateNewEpisodeStatusNumber(
        followedAnimeList: List<Anime>
    ): Int {
        var statusCount = 0
        followedAnimeList.forEach { anime ->
            if (anime.hasNewEpisode) statusCount++
        }
        return statusCount
    }
}

@Suppress("UNCHECKED_CAST")
class MainViewModelFactory(
    private val application: AnimeNotiApplication,
    private val fetchAllDatabaseItemsAsFlowUseCase: FetchAllDatabaseItemsAsFlowUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
            return MainActivityViewModel(
                fetchAllDatabaseItemsAsFlowUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

    companion object {
        fun getInstance(application: AnimeNotiApplication): MainViewModelFactory {
            return MainViewModelFactory(
                application,
                FetchAllDatabaseItemsAsFlowUseCase(application.databaseRepository)
            )
        }
    }
}