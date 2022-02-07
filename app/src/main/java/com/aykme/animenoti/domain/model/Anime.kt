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
    @Nullable @ColumnInfo(name = "released_on") val releasedOn: String?,
    @Nullable @ColumnInfo(name = "description") val description: String?,
    @Nullable @ColumnInfo(name = "next_episode_at") val nextEpisodeAt: String?,
    @Nullable @ColumnInfo(name = "episodes_viewed") val episodesViewed: Int
) {
    override fun hashCode(): Int {
        return id
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Anime) return false

        if (id != other.id) return false
        if (name != other.name) return false
        if (imageUrl != other.imageUrl) return false
        if (score != other.score) return false
        if (episodesAired != other.episodesAired) return false
        if (episodesTotal != other.episodesTotal) return false
        if (status != other.status) return false
        if (airedOn != other.airedOn) return false
        if (releasedOn != other.releasedOn) return false

        return true
    }
}
