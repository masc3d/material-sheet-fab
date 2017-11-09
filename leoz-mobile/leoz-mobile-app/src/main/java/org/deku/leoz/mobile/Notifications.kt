package org.deku.leoz.mobile

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.v4.app.NotificationCompat
import org.deku.leoz.mobile.ui.activity.StartupActivity

/**
 * Created by prangenberg on 08.11.17.
 */
class Notifications(val context: Context) {

    companion object {
        const val NOTIFICATION_ID_SERVICE_NOTIFICATION = 0
    }

    private val notificationManager by lazy { context.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager }

    val channel: Channel by lazy {
        Channel(this.context, this.notificationManager)
    }

    val showTaskIntent by lazy {
        Intent(this.context, StartupActivity::class.java).also {
            it.action = Intent.ACTION_VIEW
            it.addCategory(Intent.CATEGORY_LAUNCHER)
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    val serviceNotification by lazy {
        val pendingIntent by lazy {
            PendingIntent.getActivity(
                    this.context,
                    0,
                    showTaskIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        @Suppress("DEPRECATION")
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this.context)

        val notification = builder.setContentTitle(this.context.getString(R.string.app_name_long))
                .setContentText("${this.context.getString(R.string.app_name)} ${this.context.getString(R.string.running)}")
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .setChannelId(Channel.NOTIFICATION_CHANNEL_ID_MAIN)
                .build().also {
            it.flags += Notification.FLAG_FOREGROUND_SERVICE
        }

        NotificationObject(NOTIFICATION_ID_SERVICE_NOTIFICATION, notification, this.notificationManager)
    }

    class Channel(val context: Context, val notificationManager: NotificationManager) {
        companion object {
            const val NOTIFICATION_CHANNEL_ID_MAIN = "leoz"
        }

        val mainChannel: NotificationChannel? by lazy {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
                null
            else {
                val notificationChannel: NotificationChannel
                val channelName = ""
                val channelDescription = "mobileX Notifications"
                val importance = NotificationManager.IMPORTANCE_HIGH

                notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID_MAIN, channelName, importance).also {
                    it.description = channelDescription
                    it.enableLights(true)
                    it.lightColor = Color.RED
                    it.enableVibration(false)
                }

                this.notificationManager.createNotificationChannel(notificationChannel)
                notificationChannel
            }
        }
    }

    data class NotificationObject(
            val id: Int,
            val notification: android.app.Notification,
            private val notificationManager: NotificationManager
    ) {
        fun show() {
            this.notificationManager.notify(this.id, this.notification)
        }

        fun cancel() {
            this.notificationManager.cancel(this.id)
        }
    }
}