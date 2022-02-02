package com.aykme.animenoti.background.notification

import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.*
import androidx.core.app.NotificationManagerCompat
import com.aykme.animenoti.AnimeNotiApplication
import com.aykme.animenoti.NOTIFICATION_CHANNEL_ID
import com.aykme.animenoti.R

class WorkManagerNotification(private val application: AnimeNotiApplication) {

    private var notificationId = 100
    private var notificationSummaryId = 1000
    private val notificationGroupKey = "com.aykme.animenoti.background.notification"

    fun makeNotification(contentTitle: String, contentText: String) {

        val builder = NotificationCompat.Builder(application, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_48)
            .setContentTitle(contentTitle)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(contentText)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(LongArray(1000))
            .setGroup(notificationGroupKey)
            .setAutoCancel(true)

        val summaryBuilder = NotificationCompat.Builder(application, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_48)
            .setContentTitle(contentTitle)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(contentText)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(LongArray(1000))
            .setGroup(notificationGroupKey)
            .setGroupSummary(true)
            .setGroupAlertBehavior(GROUP_ALERT_CHILDREN)

        NotificationManagerCompat.from(application).apply {
            notify(notificationSummaryId, summaryBuilder.build())
            notify(notificationId, builder.build())
        }
        notificationSummaryId
        notificationId++
    }
}