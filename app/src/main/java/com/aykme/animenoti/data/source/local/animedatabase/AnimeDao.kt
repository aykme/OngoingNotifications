package com.aykme.animenoti.data.source.local.animedatabase

import androidx.room.*
import com.aykme.animenoti.domain.model.Anime
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimeDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(anime: Anime)

    @Update
    suspend fun update(anime: Anime)

    @Query("SELECT * FROM anime_table WHERE id = :id")
    suspend fun getItem(id: Int): Anime

    @Query("SELECT * FROM anime_table ORDER BY status DESC, name ASC")
    fun getItemsAsFlow(): Flow<List<Anime>>

    @Query("SELECT * FROM anime_table")
    suspend fun getItems(): List<Anime>

    @Query("DELETE FROM anime_table WHERE id =:id")
    suspend fun delete(id: Int)

    @Query("DELETE FROM anime_table")
    suspend fun deleteAll()

    @Query("UPDATE anime_table SET has_new_episode =:hasNewEpisode")
    suspend fun updateItemsHasNewEpisodeStatus(hasNewEpisode: Boolean)
}