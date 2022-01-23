package com.aykme.animenotifications.data.source.local.animedatabase

import androidx.room.*
import com.aykme.animenotifications.domain.model.Anime
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimeDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(anime: Anime)

    @Update
    suspend fun update(anime: Anime)

    @Query("SELECT * FROM anime_table WHERE id = :id")
    fun getItem(id: Int): Flow<Anime>

    @Query("SELECT * FROM anime_table ORDER BY name")
    fun getItems(): Flow<List<Anime>>

    @Query("DELETE FROM anime_table WHERE id =:id")
    suspend fun delete(id: Int)

    @Query("DELETE FROM anime_table")
    suspend fun deleteAll()
}