package ua.nure.nomnomsave.repository.notification

import android.app.PendingIntent

interface NotificationRepository {
    fun showNotification(text: String)
    fun showNotification(title: String?, text: String?, intent: PendingIntent?)
}