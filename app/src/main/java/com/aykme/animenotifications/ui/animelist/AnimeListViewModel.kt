package com.aykme.animenotifications.ui.animelist

import android.util.Log
import android.view.View
import androidx.lifecycle.*
import com.aykme.animenotifications.R
import com.aykme.animenotifications.databinding.FragmentAnimeListBinding
import com.aykme.animenotifications.domain.model.Anime
import com.aykme.animenotifications.domain.repository.ApiStatus
import com.aykme.animenotifications.domain.usecase.FetchOngoingAnimeListUseCase
import kotlinx.coroutines.launch
import java.lang.Exception
import java.lang.IllegalArgumentException

const val AnimeListViewModelTag = "AnimeListViewModel"
const val MIN_PAGE = 1
const val MAX_PAGE = 100000
const val PAGE_STEP = 50

class AnimeListViewModel(private val fetchOngoingAnimeListUseCase: FetchOngoingAnimeListUseCase) :
    ViewModel() {
    private val _apiStatus = MutableLiveData<ApiStatus>()
    val apiStatus: LiveData<ApiStatus> = _apiStatus
    private val _ongoingAnimeList = MutableLiveData<List<Anime>>()
    val ongoingAnimeList: LiveData<List<Anime>> = _ongoingAnimeList
    var page = 1
    var limit = 50

    init {
        getOngoingAnimeList(page, limit)
    }

    private fun getOngoingAnimeList(page: Int, limit: Int) {
        viewModelScope.launch {
            _apiStatus.value = ApiStatus.LOADING
            try {
                fetchOngoingAnimeListUseCase(page, limit).let { listAnime ->
                    _apiStatus.value = ApiStatus.DONE
                    _ongoingAnimeList.value = listAnime
                }
            } catch (e: Exception) {
                _apiStatus.value = ApiStatus.ERROR
                Log.d(AnimeListViewModelTag, "Api error")
            }
        }
    }

    fun bindApiStatus(binding: FragmentAnimeListBinding) = when (apiStatus.value) {
        ApiStatus.LOADING -> {
            binding.status.setImageResource(R.drawable.loading_animation)
            binding.status.visibility = View.VISIBLE
        }
        ApiStatus.ERROR -> {
            binding.status.setImageResource(R.drawable.ic_connection_error_24)
            binding.status.visibility = View.VISIBLE
        }
        else -> binding.status.visibility = View.GONE

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