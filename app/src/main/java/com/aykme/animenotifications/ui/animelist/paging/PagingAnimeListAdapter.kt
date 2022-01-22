package com.aykme.animenotifications.ui.animelist.paging

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.aykme.animenotifications.R
import com.aykme.animenotifications.databinding.ItemAnimeListBinding
import com.aykme.animenotifications.domain.model.Anime
import com.aykme.animenotifications.ui.animelist.AnimeListViewModel

class PagingAnimeListAdapter(
    private val context: Context,
    private val viewModel: AnimeListViewModel
) :
    PagingDataAdapter<Anime, PagingAnimeListAdapter.AnimeViewHolder>(DiffCallback) {

    class AnimeViewHolder(private val binding: ItemAnimeListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            anime: Anime,
            resources: Resources?,
            viewModel: AnimeListViewModel
        ) {
            val fullImageUrl = viewModel.getImageUrl(anime)
            val episodesTotal = viewModel.getFormattedEpisodesTitle(anime)
            viewModel.bindImage(binding.animeImage, fullImageUrl)

            binding.apply {
                animeName.text = anime.name
                animeScore.text = anime.score.toString()
                animeEpisodes.text = resources?.getString(
                    R.string.anime_episodes_aired,
                    anime.episodesAired.toString(),
                    episodesTotal
                )
                val notificationText = binding.notificationText
                val notificationOnFab = binding.notificationOnFab
                val notificationOffFab = binding.notificationOffFab
                notificationOnFab.setOnClickListener {
                    viewModel.onNotificationOnClicked(
                        anime,
                        notificationText,
                        notificationOnFab,
                        notificationOffFab
                    )
                }
                notificationOffFab.setOnClickListener {
                    viewModel.onNotificationOffClicked(
                        anime,
                        notificationText,
                        notificationOnFab,
                        notificationOffFab
                    )
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeViewHolder {
        val binding = ItemAnimeListBinding.inflate(LayoutInflater.from(parent.context))
        return AnimeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnimeViewHolder, position: Int) {
        val anime = getItem(position)
        anime?.let {
            holder.bind(anime, context.resources, viewModel)
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Anime>() {
            override fun areItemsTheSame(oldItem: Anime, newItem: Anime): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Anime, newItem: Anime): Boolean {
                return oldItem == newItem
            }
        }
    }
}