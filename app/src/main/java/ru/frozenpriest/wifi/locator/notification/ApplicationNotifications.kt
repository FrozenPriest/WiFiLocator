@file:Suppress("NOTHING_TO_INLINE")

package ru.frozenpriest.wifi.locator.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import ru.frozenpriest.wifi.locator.R

class ApplicationNotifications(private val context: Context) {

// MARK: - Methods

    init {
        createNotificationChannels()
    }

    fun createWiFiScannerRunningNotification(notificationIntent: PendingIntent): Notification {
        val contentTitle = getString(R.string.wifi_scanner)
        val contentText = getString(R.string.wifi_scanner_running)

        val notification = NotificationCompat.Builder(context, ChannelId.WIFI_SCANNER_RUNNING)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setContentIntent(notificationIntent)
            .build()

        return notification
    }

    private fun createNotificationChannels() {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val wifiScannerChannelName = context.getString(R.string.wifi_scanner_channel)

        val serviceChannel = NotificationChannel(
            ChannelId.WIFI_SCANNER_RUNNING,
            wifiScannerChannelName,
            NotificationManager.IMPORTANCE_DEFAULT,
        )

        notificationManager.createNotificationChannel(serviceChannel)
    }

    private inline fun getString(@StringRes stringResId: Int): CharSequence {
        return context.getString(stringResId)
    }

// MARK: - Constants

    private object ChannelId {
        const val WIFI_SCANNER_RUNNING = "WiFiScannerChannel"
    }
}
