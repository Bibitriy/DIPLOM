package com.example.witte.scheduleDiscipline

import android.content.Context
import org.jsoup.Jsoup
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.io.File

class ScheduleDisciplineWorker(private val context: Context) {

    fun parseSchedule(html: String): List<ScheduleDiscipline> {
        val doc = Jsoup.parse(html)
        val scheduleTables = doc.select("div.studtimetable")
        val lessons = mutableListOf<ScheduleDiscipline>()
        var currentDate: LocalDate? = null

        scheduleTables.select("div").forEach { tag ->
            when {
                "ttdate" in tag.classNames() -> {
                    currentDate = LocalDate.parse(tag.text(), DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                }
                "table" in tag.classNames() -> {
                    if (currentDate == null) throw Exception("Date for lessons not found")
                    tag.select("div.row").forEach { row ->
                        if ("head" !in row.classNames()) {
                            val cells = row.select("div.cell").map { it.text() }
                            if (cells.size >= 5) {
                                val (time, name, room, type, teacher) = cells
                                val (startTime, endTime) = time.split("-")
                                lessons.add(
                                    ScheduleDiscipline(
                                        date = currentDate!!,
                                        startTime = startTime,
                                        endTime = endTime,
                                        name = name,
                                        room = room,
                                        type = type,
                                        teacher = teacher
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        return lessons
    }

    fun saveScheduleToFile(lessons: List<ScheduleDiscipline>, fileName: String) {
        val file = File(context.filesDir, fileName)
        file.printWriter().use { out ->
            lessons.forEach { lesson ->
                out.println("${lesson.date},${lesson.startTime},${lesson.endTime},${lesson.name},${lesson.room},${lesson.type},${lesson.teacher}")
            }
        }
    }
}
