package com.example.filedownloader

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.jar.Manifest


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private lateinit var radioGroup : RadioGroup
    private lateinit var toolbar : Toolbar
    private lateinit var customButton : LoadingButton
    private lateinit var notificationUtils: NotificationUtils
    private  val requestNotificationPermission = registerForActivityResult(RequestPermission()){isGranted ->
        if(isGranted){
            //Send notification
            notificationUtils.sendNotification(getDownloadFileTitle(),downloadID)
        }else{
            Toast.makeText(this,"We don't have access to send notification",Toast.LENGTH_SHORT).show()
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar = findViewById(R.id.toolbar)
        customButton = findViewById(R.id.custom_button)
        setSupportActionBar(toolbar)
        notificationUtils = NotificationUtils(this)
        notificationManager = ContextCompat.getSystemService(this,NotificationManager::class.java) as NotificationManager
        notificationUtils.createNotificationChannel()
        //This receiver is call only the download is complete (The download progress is at 100%) so i don't now how to get the download progress
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        customButton.setOnClickListener {
            download()
        }
        radioGroup = findViewById(R.id.radio_group)
        radioGroup.setOnCheckedChangeListener {group,checkId ->
            val radioButton = findViewById<RadioButton>(checkId)
            when(checkId){
                R.id.rb_glide_download -> {
                    URL = "https://github.com/bumptech/glide"
                }
                R.id.rb_app_download -> {
                    URL = "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
                }
                R.id.rb_retrofit_download -> {
                    URL = "https://github.com/square/retrofit"
                }
            }
        }

    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if(id == downloadID){
                customButton.buttonState = ButtonState.Completed
                //TODO SEND NOTIFICATION HERE
                if(ContextCompat.checkSelfPermission(this@MainActivity,POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                    notificationUtils.sendNotification(getDownloadFileTitle(),downloadID)
                }else{
                    requestNotificationPermission.launch(POST_NOTIFICATIONS)
                }
            }
        }
    }

    private fun getDownloadFileTitle():String{
        return when(URL){
            "https://github.com/bumptech/glide"-> getString(R.string.glide_loading_text)
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"->getString(R.string.load_app_loading_text)
            else -> getString(R.string.retrofit_loading_text)
        }
    }

    private fun download() {
        if(URL == null){
            Toast.makeText(this,"Please select your file to download",Toast.LENGTH_SHORT).show()
            return
        }
        val request =
            DownloadManager.Request(Uri.parse(URL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        listenLoadingProgress()
    }
    /**
     * This function is the way a find to listen the download progress but the (customButton.buttonState) don't work when i call it
     * */
    private fun listenLoadingProgress(){

        val query = DownloadManager.Query().setFilterById(downloadID)
        var downloading = true
        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        while (downloading) {
            val cursor = downloadManager.query(query)
            cursor.moveToFirst()
            val downloadBytes = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
            val totalBites = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
            val status = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
            if(downloadBytes != -1 && totalBites != -1 && status != -1){
                val downloadedBytes = cursor.getLong(downloadBytes)
                val totalBytes = cursor.getLong(totalBites)
                val safeStatus = cursor.getInt(status)
                when(safeStatus){
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        downloading = false
                    }
                    DownloadManager.STATUS_RUNNING -> {

                    }
                }
                if(safeStatus == DownloadManager.STATUS_SUCCESSFUL){
                    downloading = false
                }
                if(totalBytes != 0L){
                    val progress = downloadedBytes * 100 / totalBytes
                    if(progress >= 0){
                        customButton.setLoadingProgress(progression = (progress/100).toFloat())
                        customButton.buttonState = ButtonState.Loading
                    }
                    Log.d("Download Progress", "$progress%")
                }

            }
            cursor.close()
        }


    }

    companion object {
        private  var URL :String? = null
//            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
    }



}
