package com.aykme.animenoti.ui.animelist.paging

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aykme.animenoti.domain.model.Anime
import com.aykme.animenoti.domain.repository.ApiStatus
import com.aykme.animenoti.domain.usecase.FetchAnnouncedAnimeListUseCase

class AnnouncedListDataSource(
    private val fetchAnnouncedAnimeListUseCase: FetchAnnouncedAnimeListUseCase,
    private val apiStatus: MutableLiveData<ApiStatus>
) : PagingSource<Int, Anime>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Anime> {
        val pageNumber = params.key ?: MIN_PAGE
        val prevKey = if (pageNumber <= MIN_PAGE) null else (pageNumber - 1)
        val nextKey = if (pageNumber <= MAX_PAGE) (pageNumber + 1) else null

        apiStatus.value = ApiStatus.LOADING
        return try {
            val resultAnimeList = fetchAnnouncedAnimeListUseCase(pageNumber, PAGE_LIMIT)
            val result = LoadResult.Page(
                data = resultAnimeList,
                prevKey = prevKey,
                nextKey = nextKey
            )
            apiStatus.value = ApiStatus.DONE
            return result
        } catch (e: Exception) {
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