package com.example.witte.userName

import android.content.Context
import com.example.witte.scheduleDiscipline.ScheduleDiscipline
import org.jsoup.Jsoup
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.io.File

class UserNameWorker(private val context: Context) {

    fun parseName(html: String): String {
        val doc = Jsoup.parse(html)
        val scheduleTables = doc.select("span.usertext.mr-1")

        return scheduleTables.text()

    }


}