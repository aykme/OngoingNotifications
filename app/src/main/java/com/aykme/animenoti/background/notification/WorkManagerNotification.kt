package com.aykme.animenoti.background.notification

import android.graphics.Bitmap
import androidx.core.app.NotificationCompat.*
import androidx.core.app.NotificationManagerCompat
import com.aykme.animenoti.AnimeNotiApplication
import com.aykme.animenoti.NOTIFICATION_CHANNEL_ID
import com.aykme.animenoti.R

class WorkManagerNotification(private val application: AnimeNotiApplication) {

    private var notificationId = 1
    private val notificationSummaryId = 0
    private val notificationGroupKey = "com.aykme.animenoti.background.notification"

    fun makeNotification(contentTitle: String, contentText: String, notificationImage: Bitmap) {

        val builder = Builder(application, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_48)
            .setContentTitle(contentTitle)
            .setStyle(
                BigTextStyle()
                    .bigText(contentText)
            )
            .setLargeIcon(notificationImage)
            .setPriority(PRIORITY_HIGH)
            .setVibrate(LongArray(1000))
            .setGroup(notificationGroupKey)
            .setAutoCancel(true)

        val summaryBuilder = Builder(application, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_48)
            .setContentTitle(contentTitle)
            .setStyle(
                BigTextStyle()
                    .bigText(contentText)
                    .setSummaryText(
                        application.resources.getString(
                            R.string.work_manager_notification_summary
                        )
                    )
            )
            .setPriority(PRIORITY_HIGH)
            .setVibrate(LongArray(1000))
            .setGroup(notificationGroupKey)
            .setGroupSummary(true)
            .setGroupAlertBehavior(GROUP_ALERT_CHILDREN)

        NotificationManagerCompat.from(application).apply {
            notify(notificationId, builder.build())
            notify(notificationSummaryId, summaryBuilder.build())
        }
        notificationSummaryId
        notificationId++
    }
}