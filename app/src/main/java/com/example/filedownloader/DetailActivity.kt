package com.example.filedownloader

import android.app.DownloadManager
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.filedownloader.databinding.ActivityDetailBinding


class DetailActivity : AppCompatActivity() {
    private  var downloadID  = -1L
    private lateinit var toolbar : Toolbar
    private lateinit var binding:ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setSupportActionBar(binding.toolbar)
        val fileName = intent.getStringExtra("FILE_NAME") ?:"..."
        downloadID  = intent.getLongExtra("DOWNLOAD_ID", -1L)
        binding.tvFileName.text = fileName
        checkDownloadStatus()
        setContentView(binding.root)
        binding.btnDone.setOnClickListener {
            finish()
        }
    }

    private fun checkDownloadStatus(){
        val downloadManager = getSystemService(DownloadManager::class.java) as DownloadManager
        var finishDownload = false
        val cursor: Cursor =
            downloadManager.query(DownloadManager.Query().setFilterById(downloadID))
        if (cursor.moveToFirst()) {
            val status = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
            val safeStatus = cursor.getInt(status)
            when (safeStatus) {
                DownloadManager.STATUS_FAILED -> {
                    binding.tvStatus.text = "Fail"
                    binding.tvStatus.setTextColor(resources.getColor(R.color.red))
                }

                DownloadManager.STATUS_PAUSED -> {
                    binding.tvStatus.text = "Pause"
                }
                DownloadManager.STATUS_PENDING -> {
                    binding.tvStatus.text = "Pending"
                }
                DownloadManager.STATUS_RUNNING -> {
                    binding.tvStatus.text = "Running"
                }
                DownloadManager.STATUS_SUCCESSFUL -> {
                    binding.tvStatus.text = "Success"
                }
            }
        }
    }
}
