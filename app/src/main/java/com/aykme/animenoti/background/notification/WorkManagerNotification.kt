package com.aykme.animenoti.background.notification

import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.aykme.animenoti.AnimeNotiApplication
import com.aykme.animenoti.NOTIFICATION_CHANNEL_ID
import com.aykme.animenoti.R

class WorkManagerNotification(private val application: AnimeNotiApplication) {

    private val notificationId = 1

    fun makeNotification(contentTitle: String, contentText: String) {
        val builder = NotificationCompat.Builder(application, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_48)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(LongArray(0))
        NotificationManagerCompat.from(application).notify(notificationId, builder.build())
    }
}