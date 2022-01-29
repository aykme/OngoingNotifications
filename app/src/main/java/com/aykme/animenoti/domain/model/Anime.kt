package com.aykme.animenoti.domain.model

import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "anime_table")
data class Anime(
    @PrimaryKey @ColumnInfo(name = "id") val id: Int,
    @Nullable @ColumnInfo(name = "name") val name: String?,
    @Nullable @ColumnInfo(name = "image_url") val imageUrl: String?,
    @Nullable @ColumnInfo(name = "score") val score: Float?,
    @Nullable @ColumnInfo(name = "episodes_aired") val episodesAired: Int?,
    @Nullable @ColumnInfo(name = "episodes_total") val episodesTotal: Int?,
    @Nullable @ColumnInfo(name = "status") val status: AnimeStatus?,
    @Nullable @ColumnInfo(name = "aired_on") val airedOn: String?,
    @Nullable @ColumnInfo(name = "released_on") val releasedOn: String?
)