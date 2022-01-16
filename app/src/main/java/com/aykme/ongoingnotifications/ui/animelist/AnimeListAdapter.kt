package com.aykme.ongoingnotifications.ui.animelist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aykme.ongoingnotifications.data.source.remote.coil.ImageDownloader
import com.aykme.ongoingnotifications.data.source.remote.shikimoriapi.BASE_URL
import com.aykme.ongoingnotifications.databinding.ItemAnimeListBinding
import com.aykme.ongoingnotifications.domain.model.Anime

class AnimeListAdapter : ListAdapter<Anime, AnimeListAdapter.AnimeViewHolder>(DiffCallback) {

    class AnimeViewHolder(private val binding: ItemAnimeListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(anime: Anime) {
            val fullImageUrl = (BASE_URL + anime.imageUrl)
            ImageDownloader.bindImage(binding.animeImage, fullImageUrl)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeViewHolder {
        val binding = ItemAnimeListBinding.inflate(LayoutInflater.from(parent.context))
        return AnimeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnimeViewHolder, position: Int) {
        val anime = getItem(position)
        holder.bind(anime)
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