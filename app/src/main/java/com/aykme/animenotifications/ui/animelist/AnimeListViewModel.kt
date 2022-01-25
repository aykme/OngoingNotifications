package com.aykme.animenotifications.ui.animelist

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.*
import androidx.paging.*
import com.aykme.animenotifications.Application
import com.aykme.animenotifications.R
import com.aykme.animenotifications.data.source.remote.coil.ImageDownloader
import com.aykme.animenotifications.data.source.remote.shikimoriapi.BASE_URL
import com.aykme.animenotifications.domain.model.Anime
import com.aykme.animenotifications.domain.repository.ApiStatus
import com.aykme.animenotifications.domain.usecase.*
import com.aykme.animenotifications.ui.animelist.paging.AnnouncedListDataSource
import com.aykme.animenotifications.ui.animelist.paging.OngoingListDataSource
import com.aykme.animenotifications.ui.animelist.paging.PagingAnimeListAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import kotlin.IllegalArgumentException


class AnimeListViewModel(
    application: Application,
    fetchOngoingAnimeListUseCase: FetchOngoingAnimeListUseCase,
    fetchAnnouncedAnimeListUseCase: FetchAnnouncedAnimeListUseCase,
    fetchAllDatabaseItemsUseCase: FetchAllDatabaseItemsUseCase,
    private val insertDatabaseItemUseCase: InsertDatabaseItemUseCase,
    private val deleteOneDatabaseItemUseCase: DeleteOneDatabaseItemUseCase
) :
    ViewModel() {

    private val _apiStatus = MutableLiveData<ApiStatus>()
    val apiStatus: LiveData<ApiStatus> = _apiStatus
    val animeStatus = MutableLiveData(AnimeStatus.ONGOING)
    val ongoingAnimeData: LiveData<PagingData<Anime>> by lazy {
        getOngoingAnimeData(fetchOngoingAnimeListUseCase)
    }
    val announcedAnimeData: LiveData<PagingData<Anime>> by lazy {
        getAnnouncedAnimeData(fetchAnnouncedAnimeListUseCase)
    }
    private val resources = application.applicationContext.resources
    val followedAnimeList: LiveData<List<Anime>> = fetchAllDatabaseItemsUseCase().asLiveData()

    private fun getOngoingAnimeData(
        fetchOngoingAnimeListUseCase: FetchOngoingAnimeListUseCase
    ):
            LiveData<PagingData<Anime>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_LIMIT)
        ) {
            OngoingListDataSource(fetchOngoingAnimeListUseCase, _apiStatus)
        }.liveData.cachedIn(viewModelScope)
    }

    private fun getAnnouncedAnimeData(
        fetchAnnouncedAnimeListUseCase: FetchAnnouncedAnimeListUseCase
    ):
            LiveData<PagingData<Anime>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_LIMIT)
        ) {
            AnnouncedListDataSource(fetchAnnouncedAnimeListUseCase, _apiStatus)
        }.liveData.cachedIn(viewModelScope)
    }

    fun bindApiStatus(status: ImageView) = when (apiStatus.value) {
        ApiStatus.LOADING -> {
            status.setImageResource(R.drawable.loading_animation)
            status.visibility = View.VISIBLE
        }
        ApiStatus.ERROR -> {
            status.setImageResource(R.drawable.ic_connection_error_24)
            status.visibility = View.VISIBLE
        }
        ApiStatus.DONE -> status.visibility = View.GONE
        else -> throw IllegalArgumentException("Unknown ApiStatus")
    }

    fun submitAnimeData(adapter: PagingAnimeListAdapter, data: PagingData<Anime>) {
        viewModelScope.launch {
            adapter.submitData(data)
        }
    }

    fun getImageUrl(anime: Anime): String {
        return BASE_URL + anime.imageUrl
    }

    fun getFormattedEpisodesField(anime: Anime): String {
        return if (anime.episodesTotal < 1) "?" else anime.episodesTotal.toString()
    }

    fun bindImage(animeImage: ImageView, fullImageUrl: String) {
        ImageDownloader.bindImage(animeImage, fullImageUrl)
    }

    fun bindNotificationFields(
        anime: Anime,
        followedAnimeList: List<Anime>,
        notificationText: TextView,
        notificationOnFab: FloatingActionButton,
        notificationOffFab: FloatingActionButton
    ) {
        viewModelScope.launch {
            if (isFollowedAnime(anime, followedAnimeList)) {
                bindNotificationOnFields(notificationText, notificationOnFab, notificationOffFab)
            } else
                bindNotificationOffFields(notificationText, notificationOnFab, notificationOffFab)
        }
    }

    private fun isFollowedAnime(anime: Anime, followedAnimeList: List<Anime>): Boolean {
        return followedAnimeList.contains(anime)
    }

    fun onNotificationOnClicked(
        anime: Anime,
        notificationText: TextView,
        notificationOnFab: FloatingActionButton,
        notificationOffFab: FloatingActionButton
    ) {
        insertIntoDatabaseAsync(anime)
        bindNotificationOnFields(notificationText, notificationOnFab, notificationOffFab)
    }

    private fun insertIntoDatabaseAsync(anime: Anime) {
        viewModelScope.launch {
            insertDatabaseItemUseCase(anime)
        }
    }

    private fun bindNotificationOnFields(
        notificationText: TextView,
        notificationOnFab: FloatingActionButton,
        notificationOffFab: FloatingActionButton
    ) {
        notificationText.text = resources.getString(R.string.notification_on_text)
        notificationOffFab.visibility = View.VISIBLE
        notificationOnFab.visibility = View.GONE
    }

    fun onNotificationOffClicked(
        anime: Anime,
        notificationText: TextView,
        notificationOnFab: FloatingActionButton,
        notificationOffFab: FloatingActionButton
    ) {
        deleteFromDatabaseAsync(anime.id)
        bindNotificationOffFields(notificationText, notificationOnFab, notificationOffFab)
    }

    private fun deleteFromDatabaseAsync(id: Int) {
        viewModelScope.launch {
            deleteOneDatabaseItemUseCase(id)
        }
    }

    private fun bindNotificationOffFields(
        notificationText: TextView,
        notificationOnFab: FloatingActionButton,
        notificationOffFab: FloatingActionButton
    ) {
        notificationText.text = resources.getString(R.string.notification_off_text)
        notificationOnFab.visibility = View.VISIBLE
        notificationOffFab.visibility = View.GONE
    }
}

class AnimeListViewModelFactory(
    private val application: Application,
    private val fetchOngoingAnimeListUseCase: FetchOngoingAnimeListUseCase,
    private val fetchAnnouncedAnimeListUseCase: FetchAnnouncedAnimeListUseCase,
    private val fetchAllDatabaseItemsUseCase: FetchAllDatabaseItemsUseCase,
    private val insertDatabaseItemUseCase: InsertDatabaseItemUseCase,
    private val deleteOneDatabaseItemUseCase: DeleteOneDatabaseItemUseCase
) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AnimeListViewModel::class.java)) {
            return AnimeListViewModel(
                application,
                fetchOngoingAnimeListUseCase,
                fetchAnnouncedAnimeListUseCase,
                fetchAllDatabaseItemsUseCase,
                insertDatabaseItemUseCase,
                deleteOneDatabaseItemUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

    companion object {
        fun getInstance(application: Application): AnimeListViewModelFactory {
            return AnimeListViewModelFactory(
                application,
                FetchOngoingAnimeListUseCase(application.apiRepository),
                FetchAnnouncedAnimeListUseCase(application.apiRepository),
                FetchAllDatabaseItemsUseCase(application.databaseRepository),
                InsertDatabaseItemUseCase(application.databaseRepository),
                DeleteOneDatabaseItemUseCase(application.databaseRepository)
            )
        }
    }

}