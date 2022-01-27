package com.aykme.animenotifications.ui.diffcallback

import androidx.recyclerview.widget.DiffUtil
import com.aykme.animenotifications.domain.model.Anime

object DiffCallback {
    val instance = object : DiffUtil.ItemCallback<Anime>() {
        override fun areItemsTheSame(oldItem: Anime, newItem: Anime): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Anime, newItem: Anime): Boolean {
            return oldItem == newItem
        }
    }
}