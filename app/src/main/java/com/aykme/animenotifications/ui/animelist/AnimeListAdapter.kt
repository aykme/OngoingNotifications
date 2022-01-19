package com.aykme.animenotifications.ui.animelist

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aykme.animenotifications.R
import com.aykme.animenotifications.data.source.remote.coil.ImageDownloader
import com.aykme.animenotifications.data.source.remote.shikimoriapi.BASE_URL
import com.aykme.animenotifications.databinding.ItemAnimeListBinding
import com.aykme.animenotifications.domain.model.Anime

class AnimeListAdapter(private val context: Context) :
    ListAdapter<Anime, AnimeListAdapter.AnimeViewHolder>(DiffCallback) {

    class AnimeViewHolder(private val binding: ItemAnimeListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(anime: Anime, resources: Resources?) {
            val fullImageUrl = (BASE_URL + anime.imageUrl)
            ImageDownloader.bindImage(binding.animeImage, fullImageUrl)
            binding.animeName.text = anime.name
            binding.animeEpisodesAired.text = resources?.getString(
                R.string.anime_episodes_aired,
                anime.episodesAired,
                anime.episodes
            )
            binding.animeScore.text = anime.score.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeViewHolder {
        val binding = ItemAnimeListBinding.inflate(LayoutInflater.from(parent.context))
        return AnimeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnimeViewHolder, position: Int) {
        val anime = getItem(position)
        val resources = context.resources
        holder.bind(anime, resources)
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