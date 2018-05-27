package org.deku.leoz.mobile.service

import android.app.Service
import org.deku.leoz.mobile.Notifications

/**
 * Created by prangenberg on 08.11.17.
 */

fun Service.startForeground(notificationObject: Notifications.NotificationObject) {
    this.startForeground(notificationObject.id, notificationObject.notification)
}