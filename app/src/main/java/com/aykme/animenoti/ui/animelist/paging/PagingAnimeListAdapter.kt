package com.aykme.animenoti.ui.animelist.paging

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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
            val episodesAired = anime.episodesAired ?: 0
            val episodesTotal = anime.episodesTotal ?: 0
            val formattedEpisodes = viewModel.getFormattedEpisodesField(
                episodesAired,
                episodesTotal,
                anime.status
            )
            val fullImageUrl = viewModel.getImageUrl(anime)
            viewModel.bindImage(binding.animeImage, fullImageUrl)

            binding.apply {
                animeName.text = anime.name ?: resources?.getString(R.string.unknown)
                animeScore.text = anime.score?.toString() ?: "0"
                animeEpisodes.text = formattedEpisodes
                viewModel.bindAnimeStatus(
                    anime.status,
                    ongoingStatus,
                    announcedStatus,
                    releasedStatus
                )
                val notificationFab = animeNotificationFab
                notificationFab.setOnClickListener {
                    viewModel.onNotificationClicked(
                        isNotificationActive,
                        anime,
                        notificationFab,
                    )
                    isNotificationActive = !isNotificationActive
                }
                followedAnimeList?.let {
                    isNotificationActive = viewModel.bindDefaultStateNotificationFab(
                        anime,
                        followedAnimeList,
                        notificationFab,
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