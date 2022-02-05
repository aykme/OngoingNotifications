package com.aykme.animenoti.ui.favorites

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.aykme.animenoti.AnimeNotiApplication
import com.aykme.animenoti.R
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
import java.text.SimpleDateFormat
import java.util.*

class FavoritesViewModel(
    private val application: AnimeNotiApplication,
    private val fetchAllDatabaseItemsAsFlowUseCase: FetchAllDatabaseItemsAsFlowUseCase,
    private val insertDatabaseItemUseCase: InsertDatabaseItemUseCase,
    private val deleteOneDatabaseItemUseCase: DeleteOneDatabaseItemUseCase,
    private val fetchAnimeByIdUseCase: FetchAnimeByIdUseCase
) : ViewModel() {

    private val tag = "FavoritesViewModel"
    private val uniqueWorkName = "OneTimeRefreshAnimeDataWork"
    private val resources = application.resources
    private val _followedAnimeList: Flow<List<Anime>> by lazy {
        fetchAllDatabaseItemsAsFlowUseCase()
    }
    val followedAnimeList: LiveData<List<Anime>> by lazy {
        _followedAnimeList.asLiveData()
    }
    //private var isDetailInfoVisible = false

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

    fun onDetainButtonClicked(
        isDetailInfoActive: Boolean,
        anime: Anime,
        detailButton: ImageButton,
        favoritesName: TextView,
        favoritesEpisodes: TextView,
        status: LinearLayout,
        favoritesNotificationFabLayout: LinearLayout,
        futureInfo: TextView
    ) {
        if (isDetailInfoActive) {
            bindDetailButtonOff(
                detailButton,
                favoritesName,
                favoritesEpisodes,
                status,
                favoritesNotificationFabLayout,
                futureInfo
            )

        } else {
            setDetailInfo(anime, futureInfo)
            bindDetailButtonOn(
                detailButton,
                favoritesName,
                favoritesEpisodes,
                status,
                favoritesNotificationFabLayout,
                futureInfo
            )
        }
    }

    private fun setDetailInfo(anime: Anime, futureInfo: TextView) {
        when (anime.status) {
            AnimeStatus.ONGOING -> {
                viewModelScope.launch {
                    val nextEpisodeAt = fetchAnimeByIdUseCase(anime.id)
                        .nextEpisodeAt
                    val formattedDate = getFormattedDate(nextEpisodeAt)
                    futureInfo.text =
                        resources.getString(R.string.next_episode_at, formattedDate)
                }
            }
            AnimeStatus.ANONS -> {
                val formattedDate = getFormattedDate(anime.airedOn)
                futureInfo.text = resources.getString(R.string.aired_on, formattedDate)
            }
            AnimeStatus.RELEASED -> {
                val formattedDate = getFormattedDate(anime.releasedOn)
                futureInfo.text = resources.getString(R.string.released_on, formattedDate)
            }
            else -> {
                futureInfo.text = resources.getString(R.string.unknown)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getFormattedDate(date: String?): String {
        return try {
            val stringToDateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale("ru"))
            val dateToStringFormatter = SimpleDateFormat("d MMM yyyy")
            val unformattedDate = stringToDateFormatter.parse(date!!)
            dateToStringFormatter.format(unformattedDate!!)
        } catch (e: Throwable) {
            Log.d(tag, resources.getString(R.string.getFormattedDateError))
            Toast.makeText(
                application,
                resources.getString(R.string.getFormattedDateError),
                Toast.LENGTH_LONG
            ).show()
            resources.getString(R.string.unknown)
        }
    }

    private fun bindDetailButtonOn(
        detailButton: ImageButton,
        favoritesName: TextView,
        favoritesEpisodes: TextView,
        status: LinearLayout,
        favoritesNotificationFabLayout: LinearLayout,
        futureInfo: TextView
    ) {
        detailButton.setImageDrawable(
            ContextCompat.getDrawable(
                application,
                R.drawable.ic_favorites_detail_off_24
            )
        )
        favoritesName.visibility = View.GONE
        favoritesEpisodes.visibility = View.GONE
        status.visibility = View.GONE
        favoritesNotificationFabLayout.visibility = View.GONE
        futureInfo.visibility = View.VISIBLE
    }

    fun bindDetailButtonOff(
        detailButton: ImageButton,
        favoritesName: TextView,
        favoritesEpisodes: TextView,
        status: LinearLayout,
        favoritesNotificationFabLayout: LinearLayout,
        futureInfo: TextView
    ) {
        detailButton.setImageDrawable(
            ContextCompat.getDrawable(
                application,
                R.drawable.ic_favorites_detail_on_24
            )
        )
        favoritesName.visibility = View.VISIBLE
        favoritesEpisodes.visibility = View.VISIBLE
        status.visibility = View.VISIBLE
        favoritesNotificationFabLayout.visibility = View.VISIBLE
        futureInfo.visibility = View.GONE
    }
}

class FavoritesViewModelFactory(
    private val application: AnimeNotiApplication,
    private val fetchAllDatabaseItemsAsFlowUseCase: FetchAllDatabaseItemsAsFlowUseCase,
    private val insertDatabaseItemUseCase: InsertDatabaseItemUseCase,
    private val deleteOneDatabaseItemUseCase: DeleteOneDatabaseItemUseCase,
    private val fetchAnimeByIdUseCase: FetchAnimeByIdUseCase
) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
            return FavoritesViewModel(
                application,
                fetchAllDatabaseItemsAsFlowUseCase,
                insertDatabaseItemUseCase,
                deleteOneDatabaseItemUseCase,
                fetchAnimeByIdUseCase
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
                DeleteOneDatabaseItemUseCase(application.databaseRepository),
                FetchAnimeByIdUseCase(application.apiRepository)
            )
        }
    }
}