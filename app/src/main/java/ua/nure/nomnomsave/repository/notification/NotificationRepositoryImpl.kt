package ua.nure.nomnomsave.repository.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import ua.nure.nomnomsave.MainActivity
import ua.nure.nomnomsave.R

class NotificationRepositoryImpl(
    private val context: Context,
) : NotificationRepository {

    private val channelId = "default channel"

    init {
        createNotificationChannel(context = context)
    }

    private fun createNotificationChannel(context: Context) {
        val name = "Default"
        val descriptionText = "Default notification"
        val importance = NotificationManager.IMPORTANCE_DEFAULT

        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(channel)
    }

    override fun showNotification(text: String) {
        showNotification(
            title = null,
            text = text,
            intent = PendingIntent.getActivity(
                context,
                1,
                Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    override fun showNotification(
        title: String?,
        text: String?,
        intent: PendingIntent?
    ) {
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.logo_light)
//            .setContentTitle(title ?: context.getString(R.string.app_name))
            .setContentText(text ?: "")
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(intent)
            .build()

        val manager = ContextCompat.getSystemService(context, NotificationManager::class.java)

        manager?.notify(1, notification)

    }

}