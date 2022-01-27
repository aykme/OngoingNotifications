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

    private var followedAnimeList: List<Anime>? = null

    class AnimeViewHolder(private val binding: ItemAnimeListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            anime: Anime,
            followedAnimeList: List<Anime>?,
            resources: Resources?,
            viewModel: AnimeListViewModel
        ) {
            val fullImageUrl = viewModel.getImageUrl(anime)
            val episodesTotal = viewModel.getFormattedEpisodesField(anime)
            viewModel.bindImage(binding.animeImage, fullImageUrl)

            binding.apply {
                animeName.text = anime.name
                animeScore.text = anime.score.toString()
                animeEpisodes.text = resources?.getString(
                    R.string.anime_episodes_aired,
                    anime.episodesAired.toString(),
                    episodesTotal
                )
                val notificationText = animeNotificationText
                val notificationOnFab = animeNotificationOnFab
                val notificationOffFab = animeNotificationOffFab
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
                followedAnimeList?.let {
                    viewModel.bindNotificationFields(
                        anime,
                        followedAnimeList,
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
            holder.bind(anime, followedAnimeList, context.resources, viewModel)
        }
    }

    fun submitFollowedAnimeList(followedAnimeList: List<Anime>) {
        this.followedAnimeList = followedAnimeList
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