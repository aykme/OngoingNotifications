package com.aykme.animenotifications.ui.favorites

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.*
import com.aykme.animenotifications.Application
import com.aykme.animenotifications.data.source.remote.coil.ImageDownloader
import com.aykme.animenotifications.data.source.remote.shikimoriapi.BASE_URL
import com.aykme.animenotifications.domain.model.Anime
import com.aykme.animenotifications.domain.usecase.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FavoritesViewModel(
    fetchAllDatabaseItemsUseCase: FetchAllDatabaseItemsUseCase,
    private val insertDatabaseItemUseCase: InsertDatabaseItemUseCase,
    private val deleteOneDatabaseItemUseCase: DeleteOneDatabaseItemUseCase
) : ViewModel() {

    private val _followedAnimeList: Flow<List<Anime>> by lazy {
        fetchAllDatabaseItemsUseCase()
    }
    val followedAnimeList: LiveData<List<Anime>> by lazy {
        _followedAnimeList.asLiveData()
    }

    fun bindPlaceholder(placeholder: View, isActive: Boolean) {
       if (isActive) {
           placeholder.visibility = View.VISIBLE
       } else {
           placeholder.visibility = View.GONE
       }
    }

    fun submitAnimeData(
        adapter: FavoritesListAdapter,
        followedAnimeList: List<Anime>
    ) {
        viewModelScope.launch {
            adapter.submitList(followedAnimeList)
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
        notificationOnFab: FloatingActionButton,
        notificationOffFab: FloatingActionButton
    ) {
        viewModelScope.launch {
                bindNotificationOnFields(notificationOnFab, notificationOffFab)
        }
    }

    fun bindAnimeStatus(
        animeStatus: String,
        ongoingStatus: TextView,
        announcedStatus: TextView,
        releasedStatus: TextView,
    ) {
        when (animeStatus) {
            AnimeStatus.ONGOING.value -> {
                ongoingStatus.visibility = View.VISIBLE
                announcedStatus.visibility = View.GONE
                releasedStatus.visibility = View.GONE
            }
            AnimeStatus.ANONS.value -> {
                announcedStatus.visibility = View.VISIBLE
                ongoingStatus.visibility = View.GONE
                releasedStatus.visibility = View.GONE
            }
            AnimeStatus.RELEASED.value -> {
                releasedStatus.visibility = View.VISIBLE
                ongoingStatus.visibility = View.GONE
                announcedStatus.visibility = View.GONE
            }
        }
    }

    fun onNotificationOnClicked(
        anime: Anime,
        notificationOnFab: FloatingActionButton,
        notificationOffFab: FloatingActionButton
    ) {
        insertIntoDatabaseAsync(anime)
        bindNotificationOnFields(notificationOnFab, notificationOffFab)
    }

    private fun insertIntoDatabaseAsync(anime: Anime) {
        viewModelScope.launch {
            insertDatabaseItemUseCase(anime)
        }
    }

    private fun bindNotificationOnFields(
        notificationOnFab: FloatingActionButton,
        notificationOffFab: FloatingActionButton
    ) {
        notificationOffFab.visibility = View.VISIBLE
        notificationOnFab.visibility = View.GONE
    }

    fun onNotificationOffClicked(
        anime: Anime,
        notificationOnFab: FloatingActionButton,
        notificationOffFab: FloatingActionButton
    ) {
        deleteFromDatabaseAsync(anime.id)
        bindNotificationOffFields(notificationOnFab, notificationOffFab)
    }

    private fun deleteFromDatabaseAsync(id: Int) {
        viewModelScope.launch {
            deleteOneDatabaseItemUseCase(id)
        }
    }

    private fun bindNotificationOffFields(
        notificationOnFab: FloatingActionButton,
        notificationOffFab: FloatingActionButton
    ) {
        notificationOnFab.visibility = View.VISIBLE
        notificationOffFab.visibility = View.GONE
    }
}

class FavoritesViewModelFactory(
    private val fetchAllDatabaseItemsUseCase: FetchAllDatabaseItemsUseCase,
    private val insertDatabaseItemUseCase: InsertDatabaseItemUseCase,
    private val deleteOneDatabaseItemUseCase: DeleteOneDatabaseItemUseCase
) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
            return FavoritesViewModel(
                fetchAllDatabaseItemsUseCase,
                insertDatabaseItemUseCase,
                deleteOneDatabaseItemUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

    companion object {
        fun getInstance(application: Application): FavoritesViewModelFactory {
            return FavoritesViewModelFactory(
                FetchAllDatabaseItemsUseCase(application.databaseRepository),
                InsertDatabaseItemUseCase(application.databaseRepository),
                DeleteOneDatabaseItemUseCase(application.databaseRepository)
            )
        }
    }
}