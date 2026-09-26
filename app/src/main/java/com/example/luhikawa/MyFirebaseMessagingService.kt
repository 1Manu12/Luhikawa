package com.example.luhikawa

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.notification?.title
            ?: remoteMessage.data["title"]
            ?: "¡Miau! Tienes tareas pendientes 🐾"

        val message = remoteMessage.notification?.body
            ?: remoteMessage.data["body"]
            ?: "¡A cumplir metas hoy! ✨"

        mostrarNotificacionConMiau(title, message)
    }

    private fun mostrarNotificacionConMiau(title: String, message: String) {
        val channelId = "canal_miau"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val soundUri: Uri = Uri.parse(
            "${ContentResolver.SCHEME_ANDROID_RESOURCE}://${packageName}/${R.raw.miau}"
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()

            val channel = NotificationChannel(
                channelId,
                "Notificaciones Miau",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Canal con sonido de maullido"
                enableVibration(true)
                setSound(soundUri, audioAttributes)
            }

            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.logolk)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }
}