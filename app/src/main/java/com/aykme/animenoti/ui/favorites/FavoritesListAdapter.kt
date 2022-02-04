package com.aykme.animenoti.ui.favorites

import android.content.Context
import android.content.res.Resources
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

    class AnimeViewHolder(private val binding: ItemFavoritesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            anime: Anime,
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
                val mainInfoContainer = mainInfoContainer
                val detailButtonOn = detailButtonOn
                val detailBuffonOff = detailButtonOff
                val notificationOnFab = favoritesNotificationOnFab
                val notificationOffFab = favoritesNotificationOffFab
                detailButtonOn.setOnClickListener {
                    viewModel.onDetailButtonOnClicked(
                        detailButtonOn,
                        detailBuffonOff,
                        mainInfoContainer
                    )
                }
                detailBuffonOff.setOnClickListener {
                    viewModel.onDetailButtonOffClicked(
                        detailButtonOn,
                        detailBuffonOff,
                        mainInfoContainer
                    )
                }
                notificationOnFab.setOnClickListener {
                    viewModel.onNotificationOnClicked(
                        anime,
                        notificationOnFab,
                        notificationOffFab
                    )
                }
                notificationOffFab.setOnClickListener {
                    viewModel.onNotificationOffClicked(
                        anime,
                        notificationOnFab,
                        notificationOffFab
                    )
                }
                viewModel.bindNotificationFields(notificationOnFab, notificationOffFab)
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
            holder.bind(anime, context.resources, viewModel)
        }
    }
}