package com.example.witte.ParsePersonalAccountData
import org.threeten.bp.LocalDate

data class Lesson(
    val date: LocalDate,
    val startTime: String,
    val endTime: String,
    val name: String,
    val room: String,
    val type: String,
    val teacher: String,

)

