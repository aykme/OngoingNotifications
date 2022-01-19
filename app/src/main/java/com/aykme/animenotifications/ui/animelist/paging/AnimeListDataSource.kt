package com.aykme.animenotifications.ui.animelist.paging

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aykme.animenotifications.domain.model.Anime
import com.aykme.animenotifications.domain.repository.ApiStatus
import com.aykme.animenotifications.domain.usecase.FetchOngoingAnimeListUseCase

const val ANIME_LIST_DATA_SOURCE_TAG = "AnimeListDataSource"
const val MIN_PAGE = 1
const val MAX_PAGE = 100000
const val PAGE_LIMIT = 50

class AnimeListDataSource(
    private val fetchOngoingAnimeListUseCase: FetchOngoingAnimeListUseCase,
    private val apiStatus: MutableLiveData<ApiStatus>
) : PagingSource<Int, Anime>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Anime> {
        val pageNumber = params.key ?: MIN_PAGE
        val prevKey = if (pageNumber <= MIN_PAGE) null else (pageNumber - 1)
        val nextKey = if (pageNumber <= MAX_PAGE) (pageNumber + 1) else null

        apiStatus.value = ApiStatus.LOADING
        return try {
            val resultAnimeList = fetchOngoingAnimeListUseCase(pageNumber, PAGE_LIMIT)
            val result = LoadResult.Page(
                data = resultAnimeList,
                prevKey = prevKey,
                nextKey = nextKey
            )
            apiStatus.value = ApiStatus.DONE
            return result
        } catch (e: Exception) {
            Log.d(ANIME_LIST_DATA_SOURCE_TAG, "Load result error")
            apiStatus.value = ApiStatus.ERROR
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Anime>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
        }
    }
}