package com.example.witte.userReport

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import io.ktor.http.Cookie
import java.io.File

class AndroidDownloader(private val context: Context) : Downloader {
    private val downloadManager = context.getSystemService(DownloadManager::class.java)

    override fun downloadFile(url: String, cookies:List<Cookie>): Long {
        val request:DownloadManager.Request = DownloadManager.Request(Uri.parse(url))
            .setMimeType("application/pdf")
            .setTitle("Downloading PDF")
            .setDescription("Downloading...")
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalFilesDir(
                context,
                Environment.DIRECTORY_DOWNLOADS,
                File.separator + "my.pdf"
            )
        request.addRequestHeader("Cookie", "${cookies[0].name}=${cookies[0].value}")


        return downloadManager.enqueue(request)
    }


}

interface Downloader {
    fun downloadFile(url: String, cookies:List<Cookie>): Long
}