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
            val fullImageUrl = viewModel.getImageUrl(anime)
            val episodesTotal = viewModel.getFormattedEpisodesField(anime)
            viewModel.bindImage(binding.favoritesImage, fullImageUrl)
            binding.apply {
                favoritesName.text = anime.name
                favoritesScore.text = anime.score.toString()
                favoritesEpisodes.text = resources?.getString(
                    R.string.anime_episodes_aired,
                    anime.episodesAired.toString(),
                    episodesTotal
                )
                viewModel.bindAnimeStatus(
                    anime.status,
                    ongoingStatus,
                    announcedStatus,
                    releasedStatus
                )
                val notificationOnFab = favoritesNotificationOnFab
                val notificationOffFab = favoritesNotificationOffFab
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