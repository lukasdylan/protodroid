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
import timber.log.Timber


/**
 * Created by Lukas Dylan on 05/08/20.
 */
interface ProtodroidNotificationListener {
    fun sendNotification(message: String)
}

internal class ProtodroidNotificationListenerImpl(private val context: Context) :
    ProtodroidNotificationListener {

    override fun sendNotification(message: String) {
        Timber.d("Notif dsini")
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

        val mBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.icon_notification)
            .setContentTitle("Test notifikasi")
            .setContentText(message)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        mBuilder.setContentIntent(pendingIntent)

        notificationManager.notify(notificationId, mBuilder.build())
    }
}