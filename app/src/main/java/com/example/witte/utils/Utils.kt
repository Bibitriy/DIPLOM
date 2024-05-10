package com.example.witte.utils

import android.content.Context
import com.example.witte.ParsePersonalAccountData.Lesson
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

class Utils private constructor() {

    companion object {
        private const val sessionFileName = "session.json"
        private const val lessonsFileName = "lessons.json"

        fun saveLogin(username: String, context: Context) {
            deleteLogin(context)
            val sessionFile = File(context.filesDir, sessionFileName)
            if (!sessionFile.exists()) {
                sessionFile.createNewFile()
            }

            val jsonObject = JSONObject().apply {
                put("username", username)
            }

            FileWriter(sessionFile).use { fileWriter ->
                fileWriter.write(jsonObject.toString())
            }
        }

        fun getUsername(context: Context): String? {
            val sessionFile = File(context.filesDir, sessionFileName)
            if (!sessionFile.exists()) {
                return null
            }

            val jsonString = sessionFile.readText()
            val jsonObject = JSONObject(jsonString)

            return try {
                jsonObject.getString("username")
            } catch (e: Exception) {
                null
            }
        }


        private fun deleteLogin(context: Context) {
            val sessionFile = File(context.filesDir, sessionFileName)
            if (sessionFile.exists()) {
                sessionFile.delete()
            }
        }

        fun saveLessons(context: Context, lessons: List<Lesson>) {
            val lessonsFile = File(context.filesDir, lessonsFileName)
            if (!lessonsFile.exists()) {
                lessonsFile.createNewFile()
            }

            val jsonArray = JSONArray()
            lessons.forEach {lesson ->
                jsonArray.put(getJsonFromLesson(lesson))
            }

            FileWriter(lessonsFile).use { fileWriter ->
                fileWriter.write(jsonArray.toString())
            }

        }

        fun lessonsExists(context: Context): Boolean {
            val lessonsFile = File(context.filesDir, lessonsFileName)
            return lessonsFile.exists()
        }

        fun loadLessonsFromJSON(context: Context): List<Lesson> {
            val lessonsFile = File(context.filesDir, lessonsFileName)
            if (!lessonsFile.exists()) {
                return emptyList()
            }

            val jsonString = lessonsFile.readText()

            val jsonArray = JSONArray(jsonString)

            return mutableListOf<Lesson>().apply {
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    add(
                        Lesson(
                            date = LocalDate.parse(jsonObject.getString("date"), DateTimeFormatter.ISO_LOCAL_DATE ),
                            startTime = jsonObject.getString("startTime"),
                            endTime = jsonObject.getString("endTime"),
                            name = jsonObject.getString("name"),
                            room = jsonObject.getString("room"),
                            type = jsonObject.getString("type"),
                            teacher = jsonObject.getString("teacher"),
                        )
                    )
                }
            }.toList()

        }

        private fun getJsonFromLesson(lesson: Lesson): JSONObject {
            return JSONObject().apply {
                put("date", lesson.date.toString())
                put("startTime", lesson.startTime)
                put("endTime", lesson.endTime)
                put("name", lesson.name)
                put("room", lesson.room)
                put("type", lesson.type)
                put("teacher", lesson.teacher)
            }
        }
    }
}