package com.aykme.animenoti.data.source.remote.coil

import android.widget.ImageView
import androidx.core.net.toUri
import coil.load
import com.aykme.animenoti.R

object ImageDownloader {
    fun bindImage(imgView: ImageView, imgUrl: String?) {
        imgUrl?.let {
            val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
            imgView.load(imgUri) {
                placeholder(R.drawable.loading_animation)
                error(R.drawable.ic_image_error_24)
            }
        }
    }
}
