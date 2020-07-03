package no.nordicsemi.android.nrfmesh.service

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Handler
import androidx.core.app.NotificationCompat
import no.nordicsemi.android.nrfmesh.R
import no.nordicsemi.android.nrfmesh.utils.bytesToHex
import no.nordicsemi.android.nrfmesh.utils.toHex
import kotlin.experimental.and

class NotifyFactory(val context: Context, var totalFile: Int) {

    val notificationID = 29182
    val finish_ID = 200
    lateinit var notificationManager: NotificationManager
    lateinit var notificationBuilder: NotificationCompat.Builder
    lateinit var notification: Notification

    @TargetApi(Build.VERSION_CODES.O)
    fun notifyProgressNotification(isStart: Boolean = true, totalFile: Int = -1) {
        if (totalFile > 0) {
            this.totalFile = totalFile
        }
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "notify backup"
        val channelTitle = "BackupChanel"
        notificationBuilder = NotificationCompat.Builder(context, channelId)
        notificationBuilder.setOngoing(true)
                .setContentTitle("touchtpodd")
                .setSmallIcon(R.drawable.ic_account_key)
                .setProgress(100, 1, false)

        if(Build.VERSION.SDK_INT>= 26)
        {
            val notificationChannel = NotificationChannel(channelId, channelTitle, NotificationManager.IMPORTANCE_LOW)
            notificationChannel.vibrationPattern = longArrayOf(0);
            notificationChannel.enableVibration(true);
            notificationChannel.setShowBadge(true)
            notificationChannel.setSound(null, null)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notification = notificationBuilder.build()
        if (isStart)// true: default
            notificationManager.notify(notificationID, notification)
    }

    // TODO Just temp for marketing video
    @TargetApi(Build.VERSION_CODES.O)
    fun updateProgressNotification(progress: Int, currentNumFile: Int = totalFile, arg: ByteArray) {
        notificationBuilder.setProgress(100, progress, false)
                .setContentTitle("XXxxxXX ** $progress ** $currentNumFile ** ${arg[0]} ** ${arg[1]} ** ${arg[2]}" )

        notification = notificationBuilder.build()
        notificationManager.notify(notificationID, notification)
        if (progress == 100 && currentNumFile == totalFile) {
            notificationManager.cancel(notificationID)
            val channelId = "finish notify backup"
            val channelTitle = "BackupChanel-Finish"
            notificationBuilder = NotificationCompat.Builder(context, channelId)
            notificationBuilder.setOngoing(false)
                    .setContentTitle("ddkdk")
                    .setSmallIcon(R.drawable.ic_account_check)

            if(Build.VERSION.SDK_INT>= 26) {
                val notificationChannel = NotificationChannel(channelId, channelTitle, NotificationManager.IMPORTANCE_HIGH)
                notificationChannel.vibrationPattern = longArrayOf(0);
                notificationChannel.enableVibration(true);
                notificationChannel.setShowBadge(true)
                notificationChannel.setSound(null, null)
                notificationManager.createNotificationChannel(notificationChannel)
            }
            val notification: Notification = notificationBuilder.build()
            notificationManager.notify(finish_ID, notification)
        }
    }

}