package com.aistudio.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.aistudio.app.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VoiceCallService : Service() {
    
    companion object {
        const val CHANNEL_ID = "voice_call_channel"
        const val NOTIFICATION_ID = 1001
        const val ACTION_START_CALL = "com.aistudio.app.START_VOICE_CALL"
        const val ACTION_END_CALL = "com.aistudio.app.END_VOICE_CALL"
        const val EXTRA_CHAT_ID = "chat_id"
    }
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_CALL -> {
                val chatId = intent.getStringExtra(EXTRA_CHAT_ID) ?: return START_NOT_STICKY
                startForeground(NOTIFICATION_ID, createNotification("通话中..."))
            }
            ACTION_END_CALL -> {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "语音通话",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "语音通话通知"
                setShowBadge(false)
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun createNotification(content: String): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("AI智能体工坊")
            .setContentText(content)
            .setSmallIcon(android.R.drawable.ic_menu_call)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }
}
