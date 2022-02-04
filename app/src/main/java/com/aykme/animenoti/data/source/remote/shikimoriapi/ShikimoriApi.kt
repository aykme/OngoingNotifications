package com.aykme.animenoti.data.source.remote.shikimoriapi

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

const val BASE_URL = "https://shikimori.one"
private const val ANIMES_APPEND_URL = "api/animes"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .build()

interface ShikimoriApi {
    companion object {
        val instance: ShikimoriApi by lazy {
            retrofit.create(ShikimoriApi::class.java)
        }
    }

    @GET(ANIMES_APPEND_URL)
    suspend fun getAnimeList(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("status") status: String? = null,
        @Query("order") order: String? = null,
        @Query("ids") ids: String? = null
    ): List<AnimeResponse>

    @GET("$ANIMES_APPEND_URL/{id}")
    suspend fun getAnimeById(@Path("id") id: Int): AnimeDetailsResponse
}
