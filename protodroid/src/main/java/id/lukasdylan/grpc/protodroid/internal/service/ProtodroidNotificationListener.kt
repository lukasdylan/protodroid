package id.lukasdylan.grpc.protodroid.internal.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import id.lukasdylan.grpc.protodroid.R
import id.lukasdylan.grpc.protodroid.internal.ui.MainActivity


/**
 * Created by Lukas Dylan on 05/08/20.
 */
interface ProtodroidNotificationListener {
    fun sendNotification(title: String, message: String, dataId: Long, serviceName: String)
}

internal class ProtodroidNotificationListenerImpl(private val context: Context) :
    ProtodroidNotificationListener {

    override fun sendNotification(
        title: String,
        message: String,
        dataId: Long,
        serviceName: String
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationId = 1
        val channelId = "protodroid-channel-id"
        val channelName = "Protodroid Channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(
                channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(mChannel)
        }

        val summaryNotification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.protodroid_icon_notification)
            .setStyle(NotificationCompat.InboxStyle())
            .setGroup("protodroid-group")
            .setGroupSummary(true)
            .build()

        val mBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.protodroid_icon_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setGroup("protodroid-group")
            .setAutoCancel(true)

        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("id", dataId)
            putExtra("service_name", serviceName)
            putExtra("open_detail", true)
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        mBuilder.setContentIntent(pendingIntent)

        notificationManager.notify(notificationId, summaryNotification)
        notificationManager.notify(System.currentTimeMillis().toInt(), mBuilder.build())
    }
}