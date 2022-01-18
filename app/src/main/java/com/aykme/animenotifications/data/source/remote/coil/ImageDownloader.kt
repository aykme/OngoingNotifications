package com.aykme.animenotifications.data.source.remote.coil

import android.widget.ImageView
import androidx.core.net.toUri
import coil.load

object ImageDownloader {
    fun bindImage(imgView: ImageView, imgUrl: String?) {
        imgUrl?.let {
            val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
            imgView.load(imgUri)
        }
    }
}
