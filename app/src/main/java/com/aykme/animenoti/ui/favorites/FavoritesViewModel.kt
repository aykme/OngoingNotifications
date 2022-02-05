package com.aykme.animenoti.ui.favorites

import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.*
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.aykme.animenoti.AnimeNotiApplication
import com.aykme.animenoti.background.workers.REFRESH_ANIME_DATA_WORK
import com.aykme.animenoti.background.workers.RefreshAnimeDataWork
import com.aykme.animenoti.data.source.remote.coil.ImageDownloader
import com.aykme.animenoti.data.source.remote.shikimoriapi.BASE_URL
import com.aykme.animenoti.domain.model.Anime
import com.aykme.animenoti.domain.model.AnimeStatus
import com.aykme.animenoti.domain.usecase.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val application: AnimeNotiApplication,
    private val fetchAllDatabaseItemsAsFlowUseCase: FetchAllDatabaseItemsAsFlowUseCase,
    private val insertDatabaseItemUseCase: InsertDatabaseItemUseCase,
    private val deleteOneDatabaseItemUseCase: DeleteOneDatabaseItemUseCase
) : ViewModel() {

    private val uniqueWorkName = "OneTimeRefreshAnimeDataWork"
    private val resources = application.resources
    private val _followedAnimeList: Flow<List<Anime>> by lazy {
        fetchAllDatabaseItemsAsFlowUseCase()
    }
    val followedAnimeList: LiveData<List<Anime>> by lazy {
        _followedAnimeList.asLiveData()
    }
    private var isMainInfoVisible = true

    fun bindPlaceholder(placeholder: View, isActive: Boolean) {
        if (isActive) {
            placeholder.visibility = View.VISIBLE
        } else {
            placeholder.visibility = View.GONE
        }
    }

    fun refreshDatabaseItems() {
        Log.d(REFRESH_ANIME_DATA_WORK, "viewModel refresh")
        val workManager = WorkManager.getInstance(application)
        val work = OneTimeWorkRequestBuilder<RefreshAnimeDataWork>().build()
        workManager.enqueueUniqueWork(
            uniqueWorkName,
            ExistingWorkPolicy.KEEP,
            work
        )
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
        val imageUrl = anime.imageUrl ?: ""
        return BASE_URL + imageUrl
    }

    fun getFormattedEpisodesField(episodesTotal: Int): String {
        return if (episodesTotal < 1) "?" else episodesTotal.toString()
    }

    fun bindImage(animeImage: ImageView, fullImageUrl: String) {
        ImageDownloader.bindImageView(animeImage, fullImageUrl)
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
        animeStatus: AnimeStatus?,
        ongoingStatus: TextView,
        announcedStatus: TextView,
        releasedStatus: TextView,
    ) {
        when (animeStatus) {
            AnimeStatus.ONGOING -> {
                ongoingStatus.visibility = View.VISIBLE
                announcedStatus.visibility = View.GONE
                releasedStatus.visibility = View.GONE
            }
            AnimeStatus.ANONS -> {
                announcedStatus.visibility = View.VISIBLE
                ongoingStatus.visibility = View.GONE
                releasedStatus.visibility = View.GONE
            }
            AnimeStatus.RELEASED -> {
                releasedStatus.visibility = View.VISIBLE
                ongoingStatus.visibility = View.GONE
                announcedStatus.visibility = View.GONE
            }
            else -> {
                releasedStatus.visibility = View.GONE
                ongoingStatus.visibility = View.GONE
                announcedStatus.visibility = View.GONE
            }
        }
    }

    fun onDetainButtonClicked(
        detailButtonOn: ImageButton,
        detailButtonOff: ImageButton,
        favoritesName: TextView,
        favoritesEpisodes: TextView,
        status: LinearLayout,
        favoritesNotificationFabLayout: LinearLayout,
        nextEpisodeAt: TextView
    ) {
        if (isMainInfoVisible) {
            isMainInfoVisible = false
            bindDetailButtonOn(
                detailButtonOn,
                detailButtonOff,
                favoritesName,
                favoritesEpisodes,
                status,
                favoritesNotificationFabLayout,
                nextEpisodeAt
            )
        } else {
            isMainInfoVisible = true
            bindDetailButtonOff(
                detailButtonOn,
                detailButtonOff,
                favoritesName,
                favoritesEpisodes,
                status,
                favoritesNotificationFabLayout,
                nextEpisodeAt
            )
        }
    }

    private fun bindDetailButtonOn(
        detailButtonOn: ImageButton,
        detailButtonOff: ImageButton,
        favoritesName: TextView,
        favoritesEpisodes: TextView,
        status: LinearLayout,
        favoritesNotificationFabLayout: LinearLayout,
        nextEpisodeAt: TextView
    ) {
        detailButtonOn.visibility = View.GONE
        detailButtonOff.visibility = View.VISIBLE
        favoritesName.visibility = View.GONE
        favoritesEpisodes.visibility = View.GONE
        status.visibility = View.GONE
        favoritesNotificationFabLayout.visibility = View.GONE
        nextEpisodeAt.visibility = View.VISIBLE
    }

    private fun bindDetailButtonOff(
        detailButtonOn: ImageButton,
        detailButtonOff: ImageButton,
        favoritesName: TextView,
        favoritesEpisodes: TextView,
        status: LinearLayout,
        favoritesNotificationFabLayout: LinearLayout,
        nextEpisodeAt: TextView
    ) {
        detailButtonOn.visibility = View.VISIBLE
        detailButtonOff.visibility = View.GONE
        favoritesName.visibility = View.VISIBLE
        favoritesEpisodes.visibility = View.VISIBLE
        status.visibility = View.VISIBLE
        favoritesNotificationFabLayout.visibility = View.VISIBLE
        nextEpisodeAt.visibility = View.GONE
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
    private val application: AnimeNotiApplication,
    private val fetchAllDatabaseItemsAsFlowUseCase: FetchAllDatabaseItemsAsFlowUseCase,
    private val insertDatabaseItemUseCase: InsertDatabaseItemUseCase,
    private val deleteOneDatabaseItemUseCase: DeleteOneDatabaseItemUseCase
) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
            return FavoritesViewModel(
                application,
                fetchAllDatabaseItemsAsFlowUseCase,
                insertDatabaseItemUseCase,
                deleteOneDatabaseItemUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

    companion object {
        fun getInstance(application: AnimeNotiApplication): FavoritesViewModelFactory {
            return FavoritesViewModelFactory(
                application,
                FetchAllDatabaseItemsAsFlowUseCase(application.databaseRepository),
                InsertDatabaseItemUseCase(application.databaseRepository),
                DeleteOneDatabaseItemUseCase(application.databaseRepository)
            )
        }
    }
}