package com.aykme.ongoingnotifications.ui.animelist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aykme.ongoingnotifications.data.source.remote.api.shikimori.ShikimoriApi
import kotlinx.coroutines.launch

const val TAG = "AnimeListViewModel"

class AnimeListViewModel: ViewModel() {
    fun testApi() {
        viewModelScope.launch {
            val api = ShikimoriApi.instance
            val animeList = api.getAnimeList(1, 50, "ongoing")
            Log.d(TAG, "Размер полученного листа: ${animeList.size}")
            val anime = animeList[0]
            Log.d(TAG, "Аниме детали: $anime")
            val image = anime.imageResponse
            Log.d(TAG, "Image детали: $image")
        }
    }
}