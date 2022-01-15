package com.aykme.ongoingnotifications.ui.animelist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aykme.ongoingnotifications.data.repository.ShikimoriApiRepository
import com.aykme.ongoingnotifications.data.source.remote.shikimoriapi.ShikimoriApi
import com.aykme.ongoingnotifications.domain.usecase.FetchOngoingAnimeListUseCase
import kotlinx.coroutines.launch

const val TAG = "AnimeListViewModel"

class AnimeListViewModel : ViewModel() {

    fun testApi() {
        viewModelScope.launch {
            val useCase =
                FetchOngoingAnimeListUseCase(ShikimoriApiRepository(ShikimoriApi.instance))
            val animeList = useCase.invoke(1, 2)
            Log.d(TAG, "Размер полученного листа: ${animeList.size}")
            val anime = animeList[0]
            Log.d(TAG, "Аниме детали: $anime")
        }
    }
}