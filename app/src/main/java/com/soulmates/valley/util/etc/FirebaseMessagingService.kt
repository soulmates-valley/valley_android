package com.soulmates.valley.util.etc

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.soulmates.valley.ValleyApplication


class FirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.notification != null && remoteMessage.data.isNotEmpty()) {
            sendNotification(remoteMessage)
        }
    }

    private fun sendNotification(remoteMessage: RemoteMessage) {
        val title = remoteMessage.data["title"]
        val message = remoteMessage.data["message"]
        val CHANNEL_ID = "ChannerID"
        val mManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val CHANNEL_NAME = "ChannerName"
            val CHANNEL_DESCRIPTION = "ChannerDescription"
            val importance = NotificationManager.IMPORTANCE_HIGH

            // add in API level 26
            val mChannel =
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
            mChannel.description = CHANNEL_DESCRIPTION
            mChannel.enableLights(true)
            mChannel.enableVibration(true)
            mChannel.vibrationPattern = longArrayOf(100, 200, 100, 200)
            mChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            mManager.createNotificationChannel(mChannel)
        }
        val builder =
            NotificationCompat.Builder(this, CHANNEL_ID)
        builder.setSmallIcon(com.soulmates.valley.R.drawable.img_icon)
        builder.setAutoCancel(true)
        builder.setDefaults(Notification.DEFAULT_ALL)
        builder.setWhen(System.currentTimeMillis())
        builder.setContentTitle(title)
        builder.setContentText(message)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            builder.setContentTitle(title)
            builder.setVibrate(longArrayOf(500, 500))
        }
        mManager.notify(0, builder.build())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        ValleyApplication.prefManager.deviceToken = token
    }
}