package com.example.witte.debtDiscipline

import android.content.Context
import com.example.witte.currentDiscipline.Discipline
import org.jsoup.Jsoup
import java.io.File

class DebtDisciplineWorker(private val context: Context) {

    fun parseDisciplines(html: String): List<Discipline> {
        val doc = Jsoup.parse(html)
        val disciplines = mutableListOf<Discipline>()

        val currentSemesterHeader = doc.selectFirst("h2.section_type:contains(Задолженности)")

        currentSemesterHeader?.nextElementSibling()?.let { disList ->
            disList.select("div.dis_block").forEach { block ->
                val name = block.select("span.dis_name a").text()
                val examType = block.select("span.reports").text()
                val info = block.select("div.dis_content div span.dis_info").text()
                val teacher = block.select("span.teachers a").text()

                disciplines.add(Discipline(name, info, teacher, examType))
            }
        }

        return disciplines
    }

    fun saveDisciplinesToFile(disciplines: List<Discipline>, fileName: String) {
        val file = File(context.filesDir, fileName)
        file.printWriter().use { out ->
            disciplines.forEach { discipline ->
                out.println("${discipline.name},${discipline.info},${discipline.teacher},${discipline.examType}")
            }
        }
    }

    fun loadDisciplinesFromFile(fileName: String): List<Discipline> {
        val file = File(context.filesDir, fileName)
        if (!file.exists()) return emptyList()

        return file.readLines().map { line ->
            val (name, info, teacher, examType) = line.split(",")
            Discipline(name, info, teacher, examType)
        }
    }
}
