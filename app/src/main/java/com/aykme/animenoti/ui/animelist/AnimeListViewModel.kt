package com.aykme.animenoti.ui.animelist

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import androidx.paging.*
import com.aykme.animenoti.AnimeNotiApplication
import com.aykme.animenoti.PAGE_LIMIT
import com.aykme.animenoti.R
import com.aykme.animenoti.data.source.remote.coil.ImageDownloader
import com.aykme.animenoti.data.source.remote.shikimoriapi.BASE_URL
import com.aykme.animenoti.domain.model.Anime
import com.aykme.animenoti.domain.model.AnimeStatus
import com.aykme.animenoti.domain.repository.ApiStatus
import com.aykme.animenoti.domain.usecase.*
import com.aykme.animenoti.ui.animelist.paging.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.IllegalArgumentException

class AnimeListViewModel(
    private val application: AnimeNotiApplication,
    private val fetchOngoingAnimeListUseCase: FetchOngoingAnimeListUseCase,
    private val fetchAnnouncedAnimeListUseCase: FetchAnnouncedAnimeListUseCase,
    fetchAllDatabaseItemsAsFlowUseCase: FetchAllDatabaseItemsAsFlowUseCase,
    private val insertDatabaseItemUseCase: InsertDatabaseItemUseCase,
    private val deleteOneDatabaseItemUseCase: DeleteOneDatabaseItemUseCase,
    private val fetchAnimeByIdUseCase: FetchAnimeByIdUseCase,
    private val fetchAnimeListBySearchUseCase: FetchAnimeListBySearchUseCase
) :
    ViewModel() {

    private val tag = "AnimeListViewModel"
    private val resources = application.applicationContext.resources
    private val _apiStatus = MutableLiveData(ApiStatus.LOADING)
    val apiStatus: LiveData<ApiStatus> = _apiStatus
    val followedAnimeList: LiveData<List<Anime>> by lazy {
        fetchAllDatabaseItemsAsFlowUseCase().asLiveData()
    }
    var dataType = AnimeDataType.ONGOING
    var searchData = ""

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

    fun submitAnimeData(
        adapter: PagingAnimeListAdapter,
        animeDataType: AnimeDataType,
    ) {
        viewModelScope.launch {
            when (animeDataType) {
                AnimeDataType.ONGOING -> getOngoingAnimeData().collectLatest {
                    adapter.submitData(it)
                }
                AnimeDataType.ANONS -> getAnnouncedAnimeData().collectLatest {
                    adapter.submitData(it)
                }
                AnimeDataType.SEARCH -> {
                    getSearchedAnimeData(searchData).collectLatest {
                        adapter.submitData(it)
                    }
                }
            }
        }
    }

    fun refreshRecyclerView(
        ongoingListAdapter: PagingAnimeListAdapter,
        announcedListAdapter: PagingAnimeListAdapter,
        searchListAdapter: PagingAnimeListAdapter
    ) {
        when (dataType) {
            AnimeDataType.ONGOING -> submitAnimeData(ongoingListAdapter, AnimeDataType.ONGOING)
            AnimeDataType.ANONS -> submitAnimeData(announcedListAdapter, AnimeDataType.ANONS)
            AnimeDataType.SEARCH -> submitAnimeData(searchListAdapter, AnimeDataType.SEARCH)
        }
    }

    private fun getOngoingAnimeData(): Flow<PagingData<Anime>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_LIMIT)
        ) {
            OngoingListDataSource(fetchOngoingAnimeListUseCase, _apiStatus)
        }.flow.cachedIn(viewModelScope)
    }

    private fun getAnnouncedAnimeData(): Flow<PagingData<Anime>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_LIMIT)
        ) {
            AnnouncedListDataSource(fetchAnnouncedAnimeListUseCase, _apiStatus)
        }.flow.cachedIn(viewModelScope)
    }

    private fun getSearchedAnimeData(search: String): Flow<PagingData<Anime>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_LIMIT)
        ) {
            SearchedListDataSource(fetchAnimeListBySearchUseCase, search, _apiStatus)
        }.flow.cachedIn(viewModelScope)
    }

    fun getImageUrl(anime: Anime): String {
        val imageUrl = anime.imageUrl ?: ""
        return BASE_URL + imageUrl
    }

    fun bindDefaultUpperMenuState(
        ongoingAnimeButton: MaterialButton,
        announcedAnimeButton: MaterialButton,
        searchAnimeButton: ImageButton,
        searchCancelButton: ImageButton,
        verticalDivider: View,
        searchInputTextLayout: RelativeLayout,
        ongoingListAdapter: PagingAnimeListAdapter
    ) {
        onOngoingAnimeButtonClicked(
            ongoingAnimeButton,
            announcedAnimeButton,
            searchAnimeButton,
            searchCancelButton,
            verticalDivider,
            searchInputTextLayout
        )
        submitAnimeData(ongoingListAdapter, AnimeDataType.ONGOING)
    }

    fun onOngoingAnimeButtonClicked(
        ongoingAnimeButton: MaterialButton,
        announcedAnimeButton: MaterialButton,
        searchAnimeButton: ImageButton,
        searchCancelButton: ImageButton,
        verticalDivider: View,
        searchInputTextLayout: RelativeLayout
    ) {
        val pinkColorId = getPinkColorId()
        val whiteTransparentColorId = getWhiteTransparentColorId()
        val searchIconOff = getSearchIconOff()
        ongoingAnimeButton.setTextColor(pinkColorId)
        announcedAnimeButton.setTextColor(whiteTransparentColorId)
        searchAnimeButton.setImageDrawable(searchIconOff)
        ongoingAnimeButton.visibility = View.VISIBLE
        announcedAnimeButton.visibility = View.VISIBLE
        verticalDivider.visibility = View.VISIBLE
        searchAnimeButton.visibility = View.VISIBLE
        searchInputTextLayout.visibility = View.GONE
        searchCancelButton.visibility = View.GONE
    }

    fun onAnnouncedAnimeButtonClicked(
        ongoingAnimeButton: MaterialButton,
        announcedAnimeButton: MaterialButton,
        searchAnimeButton: ImageButton,
        searchCancelButton: ImageButton,
        verticalDivider: View,
        searchInputTextLayout: RelativeLayout
    ) {
        val pinkColorId = getPinkColorId()
        val whiteTransparentColorId = getWhiteTransparentColorId()
        val searchIconOff = getSearchIconOff()
        ongoingAnimeButton.setTextColor(whiteTransparentColorId)
        announcedAnimeButton.setTextColor(pinkColorId)
        searchAnimeButton.setImageDrawable(searchIconOff)
        ongoingAnimeButton.visibility = View.VISIBLE
        announcedAnimeButton.visibility = View.VISIBLE
        verticalDivider.visibility = View.VISIBLE
        searchAnimeButton.visibility = View.VISIBLE
        searchInputTextLayout.visibility = View.GONE
        searchCancelButton.visibility = View.GONE
    }

    fun onSearchAnimeButtonClicked(
        ongoingAnimeButton: MaterialButton,
        announcedAnimeButton: MaterialButton,
        searchAnimeButton: ImageButton,
        searchCancelButton: ImageButton,
        verticalDivider: View,
        searchInputTextLayout: RelativeLayout
    ) {
        val whiteTransparentColorId = getWhiteTransparentColorId()
        val searchIconOn = getSearchIconOn()
        ongoingAnimeButton.setTextColor(whiteTransparentColorId)
        announcedAnimeButton.setTextColor(whiteTransparentColorId)
        searchAnimeButton.setImageDrawable(searchIconOn)
        ongoingAnimeButton.visibility = View.GONE
        announcedAnimeButton.visibility = View.GONE
        verticalDivider.visibility = View.GONE
        searchAnimeButton.visibility = View.GONE
        searchCancelButton.visibility = View.VISIBLE
        searchInputTextLayout.visibility = View.VISIBLE
    }

    fun cancelSearch(
        ongoingAnimeButton: MaterialButton,
        announcedAnimeButton: MaterialButton,
        searchAnimeButton: ImageButton,
        searchCancelButton: ImageButton,
        verticalDivider: View,
        searchInputTextLayout: RelativeLayout,
        context: Context,
        view: View
    ) {
        hideKeyboard(context, view)
        val whiteTransparentColorId = getWhiteTransparentColorId()
        val searchIconOn = getSearchIconOn()
        ongoingAnimeButton.setTextColor(whiteTransparentColorId)
        announcedAnimeButton.setTextColor(whiteTransparentColorId)
        searchAnimeButton.setImageDrawable(searchIconOn)
        ongoingAnimeButton.visibility = View.VISIBLE
        announcedAnimeButton.visibility = View.VISIBLE
        verticalDivider.visibility = View.VISIBLE
        searchAnimeButton.visibility = View.VISIBLE
        searchInputTextLayout.visibility = View.GONE
        searchCancelButton.visibility = View.GONE
    }

    fun onEnterKeyClicked(
        context: Context,
        view: View,
        keyCode: Int,
        keyEvent: KeyEvent,
        ongoingAnimeButton: MaterialButton,
        announcedAnimeButton: MaterialButton,
        searchAnimeButton: ImageButton,
        searchCancelButton: ImageButton,
        verticalDivider: View,
        searchInputTextLayout: RelativeLayout
    ): Boolean {
        if ((keyEvent.action == KeyEvent.ACTION_DOWN)
            && (keyCode == KeyEvent.KEYCODE_ENTER)
        ) {
            cancelSearch(
                ongoingAnimeButton,
                announcedAnimeButton,
                searchAnimeButton,
                searchCancelButton,
                verticalDivider,
                searchInputTextLayout,
                context,
                view
            )
            return true
        }
        return false
    }

    private fun hideKeyboard(context: Context, view: View) {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun getFormattedEpisodesField(
        episodesAired: Int,
        episodesTotal: Int,
        status: AnimeStatus?
    ): String {
        val formattedEpisodesTotal =
            if (episodesTotal < 1) "?" else episodesTotal.toString()
        val formattedEpisodesAired =
            if (status != null && status == AnimeStatus.RELEASED) {
                formattedEpisodesTotal
            } else {
                episodesAired.toString()
            }
        return resources.getString(
            R.string.anime_episodes_aired,
            formattedEpisodesAired,
            formattedEpisodesTotal
        )
    }

    fun bindImage(animeImage: ImageView, fullImageUrl: String) {
        viewModelScope.launch {
            ImageDownloader.bindImageView(animeImage, fullImageUrl)
        }
    }

    fun bindDefaultStateNotificationFab(
        anime: Anime,
        followedAnimeList: List<Anime>,
        notificationText: TextView,
        notificationFab: FloatingActionButton
    ): Boolean {
        return if (isFollowedAnime(anime, followedAnimeList)) {
            bindNotificationOnFields(notificationText, notificationFab)
            true
        } else {
            bindNotificationOffFields(notificationText, notificationFab)
            false
        }
    }

    private fun isFollowedAnime(anime: Anime, followedAnimeList: List<Anime>): Boolean {
        var result = false
        viewModelScope.launch {
            followedAnimeList.forEach {
                if (it.id == anime.id) {
                    result = true
                }
            }
        }
        return result
    }

    fun onNotificationClicked(
        isNotificationActive: Boolean,
        anime: Anime,
        notificationText: TextView,
        notificationOnFab: FloatingActionButton
    ) {
        if (isNotificationActive) {
            try {
                deleteFromDatabaseAsync(anime.id)
                bindNotificationOffFields(
                    notificationText,
                    notificationOnFab
                )
            } catch (e: Throwable) {
                e.printStackTrace()
                makeDatabaseConnectionErrorMassage()
            }
        } else {
            try {
                insertIntoDatabaseAsync(anime)
                bindNotificationOnFields(
                    notificationText,
                    notificationOnFab
                )
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
        notificationText: TextView,
        notificationFab: FloatingActionButton
    ) {
        notificationText.text = resources.getString(R.string.notification_on_text)
        notificationFab.setImageDrawable(
            ContextCompat.getDrawable(application, R.drawable.ic_notification_on_24)
        )
        val greenColorId = getGreenColorId()
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
        notificationText: TextView,
        notificationFab: FloatingActionButton
    ) {
        notificationText.text = resources.getString(R.string.notification_off_text)
        notificationFab.setImageDrawable(
            ContextCompat.getDrawable(application, R.drawable.ic_notification_off_24)
        )
        val pinkColorId = getPinkColorId()
        notificationFab.backgroundTintList = ColorStateList.valueOf(pinkColorId)
        notificationFab.rippleColor = pinkColorId
        notificationFab.contentDescription = resources.getString(
            R.string.notification_off_ic
        )
    }

    private fun getWhiteTransparentColorId(): Int {
        return ContextCompat.getColor(
            application,
            R.color.white_transparent
        )
    }

    private fun getPinkColorId(): Int {
        return ContextCompat.getColor(application, R.color.pink)
    }

    private fun getGreenColorId(): Int {
        return ContextCompat.getColor(application, R.color.green)
    }

    private fun getSearchIconOff(): Drawable {
        return ContextCompat.getDrawable(
            application,
            R.drawable.ic_anime_search_off_32
        )!!
    }

    private fun getSearchIconOn(): Drawable {
        return ContextCompat.getDrawable(
            application,
            R.drawable.ic_anime_search_on_32
        )!!
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

    class AnimeListViewModelFactory(
        private val application: AnimeNotiApplication,
        private val fetchOngoingAnimeListUseCase: FetchOngoingAnimeListUseCase,
        private val fetchAnnouncedAnimeListUseCase: FetchAnnouncedAnimeListUseCase,
        private val fetchAllDatabaseItemsAsFlowUseCase: FetchAllDatabaseItemsAsFlowUseCase,
        private val insertDatabaseItemUseCase: InsertDatabaseItemUseCase,
        private val deleteOneDatabaseItemUseCase: DeleteOneDatabaseItemUseCase,
        private val fetchAnimeByIdUseCase: FetchAnimeByIdUseCase,
        private val fetchAnimeListBySearchUseCase: FetchAnimeListBySearchUseCase
    ) :
        ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AnimeListViewModel::class.java)) {
                return AnimeListViewModel(
                    application,
                    fetchOngoingAnimeListUseCase,
                    fetchAnnouncedAnimeListUseCase,
                    fetchAllDatabaseItemsAsFlowUseCase,
                    insertDatabaseItemUseCase,
                    deleteOneDatabaseItemUseCase,
                    fetchAnimeByIdUseCase,
                    fetchAnimeListBySearchUseCase
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }

        companion object {
            fun getInstance(application: AnimeNotiApplication): AnimeListViewModelFactory {
                return AnimeListViewModelFactory(
                    application,
                    FetchOngoingAnimeListUseCase(application.apiRepository),
                    FetchAnnouncedAnimeListUseCase(application.apiRepository),
                    FetchAllDatabaseItemsAsFlowUseCase(application.databaseRepository),
                    InsertDatabaseItemUseCase(application.databaseRepository),
                    DeleteOneDatabaseItemUseCase(application.databaseRepository),
                    FetchAnimeByIdUseCase(application.apiRepository),
                    FetchAnimeListBySearchUseCase(application.apiRepository)
                )
            }
        }
    }
}