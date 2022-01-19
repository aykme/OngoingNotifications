package com.aykme.animenotifications.ui.animelist

import android.view.View
import androidx.lifecycle.*
import androidx.paging.*
import com.aykme.animenotifications.R
import com.aykme.animenotifications.databinding.FragmentAnimeListBinding
import com.aykme.animenotifications.domain.model.Anime
import com.aykme.animenotifications.domain.repository.ApiStatus
import com.aykme.animenotifications.domain.usecase.FetchOngoingAnimeListUseCase
import com.aykme.animenotifications.ui.animelist.paging.AnimeListDataSource
import com.aykme.animenotifications.ui.animelist.paging.PAGE_LIMIT
import com.aykme.animenotifications.ui.animelist.paging.PagingAnimeListAdapter
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

const val ANIME_LIST_VIEW_MODEL_TAG = "AnimeListViewModel"

class AnimeListViewModel(fetchOngoingAnimeListUseCase: FetchOngoingAnimeListUseCase) :
    ViewModel() {

    private val _apiStatus = MutableLiveData<ApiStatus>()
    val apiStatus: LiveData<ApiStatus> = _apiStatus
    private val _ongoingAnimeData: LiveData<PagingData<Anime>> = Pager(
        config = PagingConfig(pageSize = PAGE_LIMIT)
    ) {
        AnimeListDataSource(fetchOngoingAnimeListUseCase, _apiStatus)
    }.liveData
    val ongoingAnimeData = _ongoingAnimeData.cachedIn(viewModelScope)

    fun bindOngoingAnimeData(adapter: PagingAnimeListAdapter, data: PagingData<Anime>) {
        viewModelScope.launch {
            adapter.submitData(data)
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