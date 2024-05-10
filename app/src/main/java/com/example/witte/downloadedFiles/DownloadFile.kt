package com.example.witte.downloadedFiles
import android.content.Context
import com.example.witte.parseSiteWithExcel.SiteScheduleUnit
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.nio.channels.Channels

fun downloadFile(context: Context, siteScheduleUnit: SiteScheduleUnit, onDownloadComplete: () -> Unit) {
    try {
        val url = URL(siteScheduleUnit.getLink())
        val directory = File(context.filesDir, "savefiles")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val outputFile =
            File(directory, "${siteScheduleUnit.getName()}.${siteScheduleUnit.getFileExtension()}")

        outputFile.createNewFile()

        url.openStream().use {
                Channels.newChannel(it).use { rbc ->
                    FileOutputStream(outputFile).use { fos ->
                        fos.channel.transferFrom(rbc, 0, Long.MAX_VALUE)
                    }
                }
        }
        onDownloadComplete()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
