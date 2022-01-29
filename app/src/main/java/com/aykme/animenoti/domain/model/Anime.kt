package com.aykme.animenoti.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "anime_table")
data class Anime(
    @PrimaryKey @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "image_url") val imageUrl: String,
    @ColumnInfo(name = "score") val score: Float,
    @ColumnInfo(name = "episodes_aired") val episodesAired: Int,
    @ColumnInfo(name = "episodes_total") val episodesTotal: Int,
    @ColumnInfo(name = "status") val status: AnimeStatus
)