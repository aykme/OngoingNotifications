package com.aykme.animenoti.ui.animelist.paging

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aykme.animenoti.R
import com.aykme.animenoti.databinding.ItemAnimeListBinding
import com.aykme.animenoti.domain.model.Anime
import com.aykme.animenoti.ui.diffcallback.DiffCallback
import com.aykme.animenoti.ui.animelist.AnimeListViewModel

class PagingAnimeListAdapter(
    private val context: Context,
    private val viewModel: AnimeListViewModel
) :
    PagingDataAdapter<Anime, PagingAnimeListAdapter.AnimeViewHolder>(DiffCallback.instance) {

    private var followedAnimeList: List<Anime>? = null

    class AnimeViewHolder(private val binding: ItemAnimeListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            anime: Anime,
            followedAnimeList: List<Anime>?,
            resources: Resources?,
            viewModel: AnimeListViewModel
        ) {
            var isNotificationActive = false
            val episodesAired = anime.episodesAired?.toString() ?: "0"
            val episodesTotal = anime.episodesTotal ?: 0
            val formattedEpisodesTotal = viewModel.getFormattedEpisodesField(episodesTotal)
            val fullImageUrl = viewModel.getImageUrl(anime)
            viewModel.bindImage(binding.animeImage, fullImageUrl)

            binding.apply {
                animeName.text = anime.name ?: resources?.getString(R.string.unknown)
                animeScore.text = anime.score?.toString() ?: "0"
                animeEpisodes.text = resources?.getString(
                    R.string.anime_episodes_aired,
                    episodesAired,
                    formattedEpisodesTotal
                )
                val notificationText = animeNotificationText
                val notificationOnFab = animeNotificationOnFab
                val notificationOffFab = animeNotificationOffFab
                notificationOnFab.setOnClickListener {
                    viewModel.onNotificationClicked(
                        isNotificationActive,
                        anime,
                        notificationText,
                        notificationOnFab,
                        notificationOffFab
                    )
                    isNotificationActive = !isNotificationActive
                }
                notificationOffFab.setOnClickListener {
                    viewModel.onNotificationClicked(
                        isNotificationActive,
                        anime,
                        notificationText,
                        notificationOnFab,
                        notificationOffFab
                    )
                    isNotificationActive = !isNotificationActive
                }
                followedAnimeList?.let {
                    isNotificationActive = viewModel.bindDefaultStateNotificationFab(
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
}