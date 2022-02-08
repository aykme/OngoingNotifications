package com.aykme.animenoti.ui.favorites

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aykme.animenoti.R
import com.aykme.animenoti.databinding.ItemFavoritesBinding
import com.aykme.animenoti.domain.model.Anime
import com.aykme.animenoti.ui.diffcallback.DiffCallback

class FavoritesListAdapter(
    private val context: Context,
    private val viewModel: FavoritesViewModel
) : ListAdapter<Anime, FavoritesListAdapter.AnimeViewHolder>(DiffCallback.instance) {

    private var followedAnimeList: List<Anime>? = null

    class AnimeViewHolder(
        private val binding: ItemFavoritesBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {
        private val tag = "FavoritesListAdapter"
        private var isDetailInfoActive = false
        private var isNotificationActive = true

        @SuppressLint("ClickableViewAccessibility")
        fun bind(
            anime: Anime,
            followedAnimeList: List<Anime>?,
            resources: Resources?,
            viewModel: FavoritesViewModel
        ) {
            val episodesAired = anime.episodesAired?.toString() ?: "0"
            val episodesTotal = anime.episodesTotal ?: 0
            val formattedEpisodesTotal = viewModel.getFormattedEpisodesField(episodesTotal)
            val fullImageUrl = viewModel.getImageUrl(anime)
            viewModel.bindImage(binding.favoritesImage, fullImageUrl)
            binding.apply {
                favoritesName.text = anime.name ?: resources?.getString(R.string.unknown)
                favoritesScore.text = anime.score?.toString() ?: "0"
                favoritesEpisodes.text = resources?.getString(
                    R.string.anime_episodes_aired,
                    episodesAired,
                    formattedEpisodesTotal
                )
                viewModel.bindAnimeStatus(
                    anime.status,
                    ongoingStatus,
                    announcedStatus,
                    releasedStatus
                )
                val notificationFab = favoritesNotificationFab
                detailButton.setOnClickListener {
                    viewModel.onDetailButtonClicked(
                        isDetailInfoActive,
                        anime,
                        detailButton,
                        favoritesName,
                        favoritesEpisodes,
                        status,
                        notificationFab,
                        futureInfo,
                        episodesViewedTitle,
                        episodesViewedMinusButton,
                        episodesViewedNumber,
                        episodesViewedPlusButton
                    )
                    isDetailInfoActive = !isDetailInfoActive
                }
                itemView.setOnLongClickListener {
                    viewModel.onDetailButtonClicked(
                        isDetailInfoActive,
                        anime,
                        detailButton,
                        favoritesName,
                        favoritesEpisodes,
                        status,
                        notificationFab,
                        futureInfo,
                        episodesViewedTitle,
                        episodesViewedMinusButton,
                        episodesViewedNumber,
                        episodesViewedPlusButton
                    )
                    isDetailInfoActive = !isDetailInfoActive
                    return@setOnLongClickListener true
                }
                episodesViewedMinusButton.setOnTouchListener(
                    RepeatListener(500, 250) {
                        viewModel.onEpisodesViewedMinusButtonClicked(
                            anime.id, episodesViewedNumber
                        )
                    }
                )
                episodesViewedPlusButton.setOnTouchListener(
                    RepeatListener(500, 250) {
                        viewModel.onEpisodesViewedPlusButtonClicked(
                            anime.id, episodesViewedNumber
                        )
                    }
                )
                notificationFab.setOnClickListener {
                    Log.d(tag, "начало аниме ${anime.name} $isNotificationActive")
                    viewModel.onNotificationClicked(
                        isNotificationActive,
                        anime,
                        notificationFab
                    )
                    isNotificationActive = !isNotificationActive
                    Log.d(tag, "конец аниме ${anime.name} $isNotificationActive")
                }
                viewModel.bindDefaultStateNotificationFab(notificationFab)
                viewModel.bindDefaultStateInfoFields(
                    detailButton,
                    favoritesName,
                    favoritesEpisodes,
                    status,
                    notificationFab,
                    futureInfo,
                    episodesViewedTitle,
                    episodesViewedMinusButton,
                    episodesViewedNumber,
                    episodesViewedPlusButton
                )
                viewModel.bindNewEpisodeStatus(
                    anime,
                    mainInfoStroke,
                    newEpisodeBackground,
                    newEpisode
                )
                itemView.setOnClickListener {
                    viewModel.cancelNewEpisodeStatus(anime)
                }
                followedAnimeList?.let { followedAnimeList ->
                    isNotificationActive = viewModel.bindDefaultStateNotificationFab(
                        anime,
                        followedAnimeList,
                        notificationFab
                    )
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeViewHolder {
        val binding = ItemFavoritesBinding.inflate(LayoutInflater.from(parent.context))
        return AnimeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnimeViewHolder, position: Int) {
        val anime = getItem(position)
        anime?.let {
            holder.bind(anime, followedAnimeList, context.resources, viewModel)
        }
    }

    fun submitFollowedAnimeList(followedAnimeList: List<Anime>) {
        this.followedAnimeList = followedAnimeList
    }
}