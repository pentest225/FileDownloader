package com.example.filedownloader

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat

class ShowNotificationDetailReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val detailIntent = Intent(context,DetailActivity::class.java)
        detailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context?.let {context ->
            val notificationManager = ContextCompat.getSystemService(context,
                NotificationManager::class.java) as NotificationManager
            notificationManager.cancelAll()
            context.startActivity(detailIntent)
        }
    }

}
