package com.example.luhikawa

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import java.util.Calendar

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        private const val PREFS = "alarm_prefs"
        private const val KEY_HOUR = "hour"
        private const val KEY_MINUTE = "minute"
        private const val KEY_ULTIMO_MENSAJE = "ultimo_mensaje"
        private const val REQUEST_CODE = 1001

        private val MENSAJES = listOf(
            "Tu gatito está esperando 🐾" to "Las tareas no se van a hacer solas. ¿De verdad vas a dejar pasar el día?",
            "Miau... 😿" to "Tu gato te extraña. Ábrenos y marca una tarea como hecha.",
            "Psst, humano 🐱" to "Solo una tarea pequeña. Después te dejo en paz (mentira).",
            "¿Otra vez ignorándome? 😾" to "Tus pendientes siguen ahí, mirándote fijamente.",
            "Tu racha está en peligro 🔥" to "Un minuto basta para mantenerla viva. ¿Lo hacemos?",
            "Hora de ser productivo 😼" to "Tu yo de mañana te lo va a agradecer. Abre la app.",
            "Este gato no se rinde 🐈" to "Seguiré maullando hasta que revises tus tareas.",
            "Nadie está mirando... 👀" to "Bueno, yo sí. Y veo tareas sin terminar.",
            "Un pasito a la vez 🐾" to "No tienes que hacerlo todo hoy, solo empezar.",
            "Miau miau miau 🔔" to "Traducción: abre la app y organiza tu día."
        )

        fun getHora(context: Context): Int =
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt(KEY_HOUR, 8)

        fun getMinuto(context: Context): Int =
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt(KEY_MINUTE, 30)

        fun schedule(context: Context, hour: Int, minute: Int) {
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
                .putInt(KEY_HOUR, hour)
                .putInt(KEY_MINUTE, minute)
                .apply()
            scheduleNext(context)
        }

        /** Elige un mensaje al azar, sin repetir el de la vez anterior. */
        private fun elegirMensaje(context: Context): Pair<String, String> {
            val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            val ultimo = prefs.getInt(KEY_ULTIMO_MENSAJE, -1)

            var indice: Int
            do {
                indice = MENSAJES.indices.random()
            } while (indice == ultimo && MENSAJES.size > 1)

            prefs.edit().putInt(KEY_ULTIMO_MENSAJE, indice).apply()
            return MENSAJES[indice]
        }

        @SuppressLint("ScheduleExactAlarm")
        fun scheduleNext(context: Context) {
            val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            if (!prefs.contains(KEY_HOUR)) return
            val hour = prefs.getInt(KEY_HOUR, 8)
            val minute = prefs.getInt(KEY_MINUTE, 30)

            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
            }

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            // Lo que se ejecuta cuando suena la alarma
            val pendingIntent = PendingIntent.getBroadcast(
                context, REQUEST_CODE,
                Intent(context, AlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Lo que se abre al tocar el ícono de alarma en la barra de estado
            val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
                ?: Intent()
            val showIntent = PendingIntent.getActivity(
                context, 0, launchIntent, PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.setAlarmClock(
                AlarmManager.AlarmClockInfo(calendar.timeInMillis, showIntent),
                pendingIntent
            )
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        // Reinicio del teléfono: solo reprogramar
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            scheduleNext(context)
            return
        }

        // Reprogramar para mañana a la misma hora
        scheduleNext(context)

        // Android 13+: sin permiso no se puede notificar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) return

        // Mensaje variado (si llega un extra, tiene prioridad)
        val (tituloAleatorio, textoAleatorio) = elegirMensaje(context)
        val title = intent.getStringExtra("EXTRA_TITLE") ?: tituloAleatorio
        val message = intent.getStringExtra("EXTRA_MESSAGE") ?: textoAleatorio

        val channelId = "canal_miau_reiterativo"
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val soundUri: Uri = Uri.parse(
            "${ContentResolver.SCHEME_ANDROID_RESOURCE}://${context.packageName}/raw/miau"
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()

            val channel = NotificationChannel(
                channelId, "Recordatorios Constantes", NotificationManager.IMPORTANCE_HIGH
            ).apply { setSound(soundUri, audioAttributes) }

            notificationManager.createNotificationChannel(channel)
        }

        // Al tocar la notificación, abrir la app
        val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
            ?.apply { flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP }
            ?: Intent()
        val contentIntent = PendingIntent.getActivity(
            context, 0, launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.logolk)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setAutoCancel(true)
            .setSound(soundUri)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(contentIntent)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}