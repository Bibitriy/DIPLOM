package com.example.witte.userGrades

import android.content.Context
import android.util.Log
import com.example.witte.scheduleDiscipline.ScheduleDiscipline
import org.jsoup.Jsoup
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.io.File

class UserGradesWorker(private val context: Context) {

    fun parseGrades(html: String): List<UserGradesModel> {
        var list:MutableList<UserGradesModel> = ArrayList<UserGradesModel>()
        val doc = Jsoup.parse(html)
        val scheduleTables1 = doc.select("td.cell.c0")
        val scheduleTables2 = doc.select("td.cell.c1")

        for(i in 2..<scheduleTables1.size){
            list.add(UserGradesModel(scheduleTables1[i].text(), scheduleTables2[i].text()))
        }


        Log.d("check_grades", "parseGradesa: "+list.size)
        return list

    }


}