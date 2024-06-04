package com.example.witte.scheduleDiscipline
import org.threeten.bp.LocalDate

data class ScheduleDiscipline(
    val date: LocalDate,
    val startTime: String,
    val endTime: String,
    val name: String,
    val room: String,
    val type: String,
    val teacher: String,
)

