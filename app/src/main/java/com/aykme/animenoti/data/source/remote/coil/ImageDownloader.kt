package com.aykme.animenoti.data.source.remote.coil

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import androidx.core.net.toUri
import coil.ImageLoader
import coil.load
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.aykme.animenoti.R

object ImageDownloader {
    fun bindImageView(imgView: ImageView, imgUrl: String?) {
        imgUrl?.let {
            val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
            imgView.load(imgUri) {
                placeholder(R.drawable.loading_animation)
                error(R.drawable.ic_image_error_24)
            }
        }
    }

    suspend fun fetchBitmap(context: Context, imgUrl: String?): Bitmap {
        return try {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(imgUrl)
                .target { result ->
                    (result as BitmapDrawable).bitmap
                }
                .build()

            val result = (loader.execute(request) as SuccessResult).drawable
            (result as BitmapDrawable).bitmap
        } catch (e: Throwable) {
            BitmapFactory.decodeResource(
                context.applicationContext.resources,
                R.drawable.ic_notification_96
            )
        }
    }
}
