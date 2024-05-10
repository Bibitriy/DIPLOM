package com.example.witte.ParsePersonalAccountData

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRedirect
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.request.*
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.statement.*
import io.ktor.http.*
import org.jsoup.Jsoup
import java.net.URL
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

class AccountData(private var username: String?, private var password: String?) {
    private var loginURL = URL("https://e.muiv.ru/login/index.php")
    private var scheduleURL = URL("https://e.muiv.ru/local/student_timetable/view.php")
    private val client: HttpClient = createEmptyClient()

    suspend fun createSession() {
        val initialResponse: HttpResponse = client.request(loginURL) {
            method = HttpMethod.Get
        }

        val loginToken = getLoginToken(initialResponse)
        client.post(loginURL) {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(FormDataContent(Parameters.build {
                append("anchor", "")
                append("logintoken", loginToken)
                append("username", username!!)
                append("password", password!!)
            }))
        }
    }

    suspend fun getSchedule(): List<Lesson> {
        createSession()
        val schedulePageResponse = client.get(scheduleURL)
        isPageError(schedulePageResponse, scheduleURL)

        return parseSchedule(schedulePageResponse.bodyAsText())
    }

    private suspend fun getLoginToken(response: HttpResponse): String {
        val loginDoc = Jsoup.parse(response.bodyAsText())
        val token = loginDoc.select("input[type=hidden][name=logintoken]").first()
        if (token == null) {
            throw LoginTokenException("login token noy found")
        } else {
            return token.attr("value")
        }
    }

    private  fun parseSchedule(html: String): List<Lesson> {
        val doc = Jsoup.parse(html)
        val scheduleTables = doc.select("div.studtimetable")
        val lessons = mutableListOf<Lesson>()
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
                                lessons.add(Lesson(
                                    date = currentDate!!,
                                    startTime = startTime,
                                    endTime = endTime,
                                    name = name,
                                    room = room,
                                    type = type,
                                    teacher = teacher
                                ))
                            }
                        }
                    }
                }
            }
        }

        return lessons
    }

    private suspend fun isPageError(response: HttpResponse, conURL: URL) {
        if (response.status != HttpStatusCode.OK) {
            println("code = ${response.status}")
            throw ConnectionErrorException(conURL.toString())
        }
        val doc = Jsoup.parse(response.bodyAsText())
        val error = doc.select("div[class=alert alert-danger]").first()

        when(error?.text()) {
            null -> return
            "Время Вашей сессии истекло. Войдите в систему еще раз." -> throw CookieExpiredException()
            "Неверный логин или пароль, попробуйте заново." -> throw InvalidAccountDataException()
        }
    }

    private fun getUserAgent(): String {
        return "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36"
    }

    private fun createEmptyClient(): HttpClient {
        return HttpClient() {
            install(HttpCookies) {
                storage = AcceptAllCookiesStorage()
            }
            install(UserAgent) {
                agent = getUserAgent()
            }
            install(HttpRedirect) {
                checkHttpMethod = true
                allowHttpsDowngrade = false
            }
        }
    }

    companion object {
        class CookieExpiredException() : Exception() {}

        class InvalidAccountDataException() : Exception() {}

        class LoginTokenException(message: String) : Exception(message) {}

        class ConnectionErrorException(message: String) : Exception(message) {}
    }
}