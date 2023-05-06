/*
 * Copyright (C) 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.filedownloader

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.RemoteInput
import androidx.core.content.getSystemService
import java.util.UUID

class NotificationUtils (val context: Context){
    private val channelId = "DownloadChannelID"
    private val channelName = "DownloadChannelName"
    private val notificationManager = context.getSystemService(NotificationManager::class.java) as NotificationManager

    fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(channelId,channelName,
                NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Notification Channel for notif when downloading is done "
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
    fun sendNotification(notificationContent:String,downloadId:Long){
            val notificationID = Math.random().toInt()
            val detailIntent = Intent(context,DetailActivity::class.java)
            detailIntent.putExtra("FILE_NAME",notificationContent)
            detailIntent.putExtra("DOWNLOAD_ID",downloadId)
            detailIntent.putExtra("NOTIFICATION_ID",notificationID)
            val detailPendingIntent = PendingIntent.getActivity(context,notificationID,detailIntent,PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            val notificationBuilder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_assistant_black_24dp)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(notificationContent)
                .setContentIntent(detailPendingIntent)
                .setAutoCancel(true)
                .apply {
                    addAction(R.drawable.ic_assistant_black_24dp,"Cheque the status",detailPendingIntent)
                }

            notificationManager.notify(notificationID,notificationBuilder.build())
    }
}
