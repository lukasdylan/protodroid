package id.lukasdylan.grpc.protodroid.internal.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import id.lukasdylan.grpc.protodroid.R
import id.lukasdylan.grpc.protodroid.internal.ui.DetailScreen

/**
 * Created by Lukas Dylan on 05/08/20.
 */
internal interface ProtodroidNotificationListener {
    fun sendNotification(title: String, message: String, dataId: Long, serviceName: String)
}

internal class ProtodroidNotificationListenerImpl(private val context: Context) :
    ProtodroidNotificationListener {

    private val notificationIdMap = mutableListOf<String>()

    override fun sendNotification(
        title: String,
        message: String,
        dataId: Long,
        serviceName: String,
    ) {
        val notificationManager = NotificationManagerCompat.from(context)

        val channelId = "protodroid-channel-id"
        val channelName = "Protodroid Channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(
                channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(mChannel)
        }

        val intent = Intent(Intent.ACTION_VIEW, DetailScreen.deeplink(dataId.toString()).toUri()).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            dataId.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.protodroid_icon_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setSilent(true)
            .build()

        val notificationId = getNotificationIdByServiceName(serviceName)
        notificationManager.cancel(notificationId) // cancel previous notification
        notificationManager.notify(notificationId, builder)
    }

    private fun getNotificationIdByServiceName(serviceName: String): Int {
        // only populate service names to list of string, then use it's index as notification ID
        val currentId = notificationIdMap.indexOf(serviceName)
        return if (currentId == -1) {
            notificationIdMap.add(serviceName)
            notificationIdMap.lastIndex
        } else {
            currentId
        }
    }
}