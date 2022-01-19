package com.aykme.animenotifications.ui.animelist.paging

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.aykme.animenotifications.R
import com.aykme.animenotifications.data.source.remote.coil.ImageDownloader
import com.aykme.animenotifications.data.source.remote.shikimoriapi.BASE_URL
import com.aykme.animenotifications.databinding.ItemAnimeListBinding
import com.aykme.animenotifications.domain.model.Anime

class PagingAnimeListAdapter(private val context: Context) :
    PagingDataAdapter<Anime, PagingAnimeListAdapter.AnimeViewHolder>(DiffCallback) {

    class AnimeViewHolder(private val binding: ItemAnimeListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(anime: Anime, resources: Resources?) {
            val fullImageUrl = (BASE_URL + anime.imageUrl)
            ImageDownloader.bindImage(binding.animeImage, fullImageUrl)
            binding.animeName.text = anime.name
            binding.animeScore.text = anime.score.toString()

            val episodesTotal = if (anime.episodesTotal < 1) "?" else anime.episodesTotal.toString()
            binding.animeEpisodes.text = resources?.getString(
                R.string.anime_episodes_aired,
                anime.episodesAired.toString(),
                episodesTotal
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeViewHolder {
        val binding = ItemAnimeListBinding.inflate(LayoutInflater.from(parent.context))
        return AnimeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnimeViewHolder, position: Int) {
        val anime = getItem(position)
        anime?.let {
            val resources = context.resources
            holder.bind(anime, resources)
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