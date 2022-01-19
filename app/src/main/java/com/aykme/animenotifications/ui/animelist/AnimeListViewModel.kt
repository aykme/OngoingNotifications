package com.aykme.animenotifications.ui.animelist

import android.util.Log
import androidx.lifecycle.*
import com.aykme.animenotifications.domain.model.Anime
import com.aykme.animenotifications.domain.usecase.FetchOngoingAnimeListUseCase
import kotlinx.coroutines.launch
import java.lang.Exception
import java.lang.IllegalArgumentException

const val AnimeListViewModelTag = "AnimeListViewModel"

class AnimeListViewModel(private val fetchOngoingAnimeListUseCase: FetchOngoingAnimeListUseCase) :
    ViewModel() {
    private val _ongoingAnimeList = MutableLiveData<List<Anime>>()
    val ongoingAnimeList: LiveData<List<Anime>> = _ongoingAnimeList
    var page = 1
    var limit = 50

    init {
        getOngoingAnimeList(page, limit)
    }

    private fun getOngoingAnimeList(page: Int, limit: Int) {
        viewModelScope.launch {
            try {
                fetchOngoingAnimeListUseCase(page, limit).let { listAnime ->
                    _ongoingAnimeList.value = listAnime
                }
            } catch (e: Exception) {
                Log.d(AnimeListViewModelTag, "Api error")
            }
        }
    }
}

class AnimeListViewModelFactory(
    private val fetchOngoingAnimeListUseCase:
    FetchOngoingAnimeListUseCase
) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AnimeListViewModel::class.java)) {
            return AnimeListViewModel(fetchOngoingAnimeListUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}