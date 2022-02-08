package com.aykme.animenoti.ui.favorites

import android.annotation.SuppressLint
import android.content.res.ColorStateList
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
import com.google.android.material.card.MaterialCardView
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
    private val fetchDatabaseItemUseCase: FetchDatabaseItemUseCase,
    private val updateDatabaseItemUseCase: UpdateDatabaseItemUseCase,
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

    fun bindDefaultStateInfoFields(
        detailButton: ImageButton,
        favoritesName: TextView,
        favoritesEpisodes: TextView,
        status: LinearLayout,
        notificationFab: FloatingActionButton,
        futureInfo: TextView,
        episodesViewedTitle: TextView,
        episodesViewedMinusButton: ImageButton,
        episodesViewedNumber: TextView,
        episodesViewedPlusButton: ImageButton
    ) {
        setVisibilityDetailButtonOff(
            detailButton,
            favoritesName,
            favoritesEpisodes,
            status,
            notificationFab,
            futureInfo,
            episodesViewedTitle,
            episodesViewedMinusButton,
            episodesViewedNumber,
            episodesViewedPlusButton
        )
    }

    fun bindDefaultStateNotificationFab(
        notificationFab: FloatingActionButton
    ) {
        viewModelScope.launch {
            bindNotificationOnFields(notificationFab)
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

    fun onNotificationClicked(
        isNotificationActive: Boolean,
        anime: Anime,
        notificationFab: FloatingActionButton
    ) {
        if (isNotificationActive) {
            try {
                deleteFromDatabaseAsync(anime.id)
                bindNotificationOffFields(notificationFab)
            } catch (e: Throwable) {
                e.printStackTrace()
                makeDatabaseConnectionErrorMassage()
            }
        } else {
            try {
                insertIntoDatabaseAsync(anime)
                bindNotificationOnFields(notificationFab)
            } catch (e: Throwable) {
                e.printStackTrace()
                makeDatabaseConnectionErrorMassage()
            }
        }
    }

    private fun insertIntoDatabaseAsync(anime: Anime) {
        viewModelScope.launch {
            insertDatabaseItemUseCase(anime)
        }
    }

    private fun bindNotificationOnFields(
        notificationFab: FloatingActionButton
    ) {
        notificationFab.setImageDrawable(
            ContextCompat.getDrawable(application, R.drawable.ic_notification_on_24)
        )
        val greenColorId = ContextCompat.getColor(application, R.color.green)
        notificationFab.backgroundTintList = ColorStateList.valueOf(greenColorId)
        notificationFab.rippleColor = greenColorId
        notificationFab.contentDescription = resources.getString(
            R.string.notification_on_ic
        )
    }

    private fun deleteFromDatabaseAsync(id: Int) {
        viewModelScope.launch {
            deleteOneDatabaseItemUseCase(id)
        }
    }

    private fun bindNotificationOffFields(
        notificationFab: FloatingActionButton
    ) {
        notificationFab.setImageDrawable(
            ContextCompat.getDrawable(application, R.drawable.ic_notification_off_24)
        )
        val pinkColorId = ContextCompat.getColor(application, R.color.pink)
        notificationFab.backgroundTintList = ColorStateList.valueOf(pinkColorId)
        notificationFab.rippleColor = pinkColorId
        notificationFab.contentDescription = resources.getString(
            R.string.notification_off_ic
        )
    }

    fun onDetailButtonClicked(
        isDetailInfoActive: Boolean,
        anime: Anime,
        detailButton: ImageButton,
        favoritesName: TextView,
        favoritesEpisodes: TextView,
        status: LinearLayout,
        notificationFab: FloatingActionButton,
        futureInfo: TextView,
        episodesViewedTitle: TextView,
        episodesViewedMinusButton: ImageButton,
        episodesViewedNumber: TextView,
        episodesViewedPlusButton: ImageButton
    ) {
        if (isDetailInfoActive) {
            setVisibilityDetailButtonOff(
                detailButton,
                favoritesName,
                favoritesEpisodes,
                status,
                notificationFab,
                futureInfo,
                episodesViewedTitle,
                episodesViewedMinusButton,
                episodesViewedNumber,
                episodesViewedPlusButton
            )

        } else {
            bindFutureInfoFields(anime, futureInfo)
            bindEpisodesViewedFields(anime.id, episodesViewedNumber)
            setVisibilityDetailButtonOn(
                detailButton,
                favoritesName,
                favoritesEpisodes,
                status,
                notificationFab,
                futureInfo,
                episodesViewedTitle,
                episodesViewedMinusButton,
                episodesViewedNumber,
                episodesViewedPlusButton
            )
        }
    }

    private fun bindFutureInfoFields(anime: Anime, futureInfo: TextView) {
        when (anime.status) {
            AnimeStatus.ONGOING -> {
                viewModelScope.launch {
                    try {
                        val nextEpisodeAt = fetchAnimeByIdUseCase(anime.id)
                            .nextEpisodeAt
                        val formattedDate = getFormattedDate(nextEpisodeAt)
                        futureInfo.text =
                            resources.getString(R.string.next_episode_at, formattedDate)
                    } catch (e: Throwable) {
                        e.printStackTrace()
                        futureInfo.text = resources.getString(R.string.unknown)
                        makeInternetConnectionErrorMassage()
                    }
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
            resources.getString(R.string.unknown)
        }
    }

    private fun bindEpisodesViewedFields(animeId: Int, episodesViewedNumber: TextView) {
        viewModelScope.launch {
            val databaseItem = fetchDatabaseItemUseCase(animeId)
            episodesViewedNumber.text = databaseItem.episodesViewed.toString()
        }
    }

    fun onEpisodesViewedMinusButtonClicked(animeId: Int, episodesViewedNumber: TextView) {
        viewModelScope.launch {
            val databaseItem = fetchDatabaseItemUseCase(animeId)
            val episodesViewed = databaseItem.episodesViewed
            if (episodesViewed > 0) {
                val updateItem = databaseItem.copy(
                    episodesViewed = episodesViewed - 1
                )
                updateDatabaseItemUseCase(updateItem)
                bindEpisodesViewedFields(animeId, episodesViewedNumber)
            }
        }
    }

    fun onEpisodesViewedPlusButtonClicked(animeId: Int, episodesViewedNumber: TextView) {
        viewModelScope.launch {
            val databaseItem = fetchDatabaseItemUseCase(animeId)
            val episodesViewed = databaseItem.episodesViewed
            val updateItem = databaseItem.copy(
                episodesViewed = episodesViewed + 1
            )
            updateDatabaseItemUseCase(updateItem)
            bindEpisodesViewedFields(animeId, episodesViewedNumber)
        }
    }

    private fun setVisibilityDetailButtonOn(
        detailButton: ImageButton,
        favoritesName: TextView,
        favoritesEpisodes: TextView,
        status: LinearLayout,
        notificationFab: FloatingActionButton,
        futureInfo: TextView,
        episodesViewedTitle: TextView,
        episodesViewedMinusButton: ImageButton,
        episodesViewedNumber: TextView,
        episodesViewedPlusButton: ImageButton
    ) {
        detailButton.setImageDrawable(
            ContextCompat.getDrawable(
                application,
                R.drawable.ic_favorites_detail_off_button_24
            )
        )
        detailButton.contentDescription = resources.getString(
            R.string.favorites_detail_off_button
        )
        favoritesName.visibility = View.GONE
        favoritesEpisodes.visibility = View.GONE
        status.visibility = View.GONE
        notificationFab.visibility = View.GONE
        futureInfo.visibility = View.VISIBLE
        episodesViewedTitle.visibility = View.VISIBLE
        episodesViewedMinusButton.visibility = View.VISIBLE
        episodesViewedNumber.visibility = View.VISIBLE
        episodesViewedPlusButton.visibility = View.VISIBLE
    }

    private fun setVisibilityDetailButtonOff(
        detailButton: ImageButton,
        favoritesName: TextView,
        favoritesEpisodes: TextView,
        status: LinearLayout,
        notificationFab: FloatingActionButton,
        futureInfo: TextView,
        episodesViewedTitle: TextView,
        episodesViewedMinusButton: ImageButton,
        episodesViewedNumber: TextView,
        episodesViewedPlusButton: ImageButton
    ) {
        detailButton.setImageDrawable(
            ContextCompat.getDrawable(
                application,
                R.drawable.ic_favorites_detail_on_button_24
            )
        )
        detailButton.contentDescription = resources.getString(
            R.string.favorites_detail_on_button
        )
        favoritesName.visibility = View.VISIBLE
        favoritesEpisodes.visibility = View.VISIBLE
        status.visibility = View.VISIBLE
        notificationFab.visibility = View.VISIBLE
        futureInfo.visibility = View.GONE
        episodesViewedTitle.visibility = View.GONE
        episodesViewedMinusButton.visibility = View.GONE
        episodesViewedNumber.visibility = View.GONE
        episodesViewedPlusButton.visibility = View.GONE
    }

    fun bindNewEpisodeStatus(anime: Anime, mainInfoStroke: MaterialCardView) {
        if (anime.hasNewEpisode) {
            val silverColor = ContextCompat.getColor(application, R.color.silver)
            mainInfoStroke.backgroundTintList = ColorStateList.valueOf(silverColor)
            val updateItem = anime.copy(hasNewEpisode = false)
            viewModelScope.launch {
                updateDatabaseItemUseCase(updateItem)
            }
        } else {
            val greyColor = ContextCompat.getColor(application, R.color.grey)
            mainInfoStroke.backgroundTintList = ColorStateList.valueOf(greyColor)
        }
    }

    private fun makeDatabaseConnectionErrorMassage() {
        Log.d(tag, resources.getString(R.string.database_access_error))
        Toast.makeText(
            application,
            resources.getString(R.string.database_access_error),
            Toast.LENGTH_LONG
        ).show()
    }

    private fun makeInternetConnectionErrorMassage() {
        Log.d(tag, resources.getString(R.string.internet_connection_error))
        Toast.makeText(
            application,
            resources.getString(R.string.internet_connection_error),
            Toast.LENGTH_LONG
        ).show()
    }
}

class FavoritesViewModelFactory(
    private val application: AnimeNotiApplication,
    private val fetchAllDatabaseItemsAsFlowUseCase: FetchAllDatabaseItemsAsFlowUseCase,
    private val insertDatabaseItemUseCase: InsertDatabaseItemUseCase,
    private val deleteOneDatabaseItemUseCase: DeleteOneDatabaseItemUseCase,
    private val fetchDatabaseItemUseCase: FetchDatabaseItemUseCase,
    private val updateDatabaseItemUseCase: UpdateDatabaseItemUseCase,
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
                fetchDatabaseItemUseCase,
                updateDatabaseItemUseCase,
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
                FetchDatabaseItemUseCase(application.databaseRepository),
                UpdateDatabaseItemUseCase(application.databaseRepository),
                FetchAnimeByIdUseCase(application.apiRepository)
            )
        }
    }
}