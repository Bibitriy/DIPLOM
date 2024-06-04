package com.example.witte.session

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpRedirect
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.cookies.cookies
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Cookie
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.http.Url
import io.ktor.http.contentType
import kotlinx.coroutines.runBlocking
import org.jsoup.Jsoup
import java.io.File

class SessionManager(
    private val context: Context,
    private val username: String,
    private val password: String
) {
    private val loginURL = Url("https://e.muiv.ru/login/index.php")
    private var client: HttpClient

    init {
        client = createHttpClient()
    }

    suspend fun createSession(): String {
        val initialResponse: HttpResponse = client.request(loginURL) {
            method = HttpMethod.Get
        }

        val loginToken = getLoginToken(initialResponse)
        val sessionResponse = client.post(loginURL) {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(FormDataContent(Parameters.build {
                append("anchor", "")
                append("logintoken", loginToken)
                append("username", username)
                append("password", password)
            }))
        }

        checkForErrors(sessionResponse)

        saveSessionToFile()

        return sessionResponse.bodyAsText()
    }

    private suspend fun getLoginToken(response: HttpResponse): String {
        val loginDoc = Jsoup.parse(response.bodyAsText())
        return loginDoc.select("input[type=hidden][name=logintoken]").attr("value")
    }

    private fun saveSessionToFile() {
        val cookies = runBlocking {
            client.cookies(loginURL)
        }
        val file = File(context.filesDir, "session.txt")
        file.printWriter().use { out ->
            cookies.forEach { cookie ->
                out.println("${cookie.name}=${cookie.value}")
            }
        }
    }


    lateinit var cookies:List<Cookie>



    fun loadSessionFromFile(url: Url = loginURL) {
        val file = File(context.filesDir, "session.txt")
        if (!file.exists()) return

        cookies = file.readLines().map { line ->
            val (name, value) = line.split("=")
            Cookie(name, value)
        }
        Log.d("check_cookies", "downloadFileSession: ${cookies.size}")

        client = createHttpClientWithCookies(url, cookies)

    }

    private fun createHttpClientWithCookies(url: Url, cookies: List<Cookie>): HttpClient {
        return HttpClient(CIO) {
            install(HttpCookies) {
                storage = AcceptAllCookiesStorage().apply {
                    cookies.forEach { cookie ->
                        runBlocking {
                            addCookie(url, cookie)
                        }
                    }
                }
            }
            install(UserAgent) {
                agent =
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36"
            }
            install(HttpRedirect) {
                checkHttpMethod = true
                allowHttpsDowngrade = false
            }
        }
    }

    suspend fun getPageContent(url: Url): String {
        loadSessionFromFile(url)
        val response: HttpResponse = client.get(url)
        checkForErrors(response)
        return response.bodyAsText()
    }



    private suspend fun checkForErrors(response: HttpResponse) {
        if (response.status != HttpStatusCode.OK && response.status != HttpStatusCode.SeeOther) {
            throw Exception("Connection error: ${response.status}")
        }
        if (response.status == HttpStatusCode.SeeOther && response.headers["Location"] == "https://e.muiv.ru/login/index.php") {
            throw Exception("Failed to create session")
        }
        val doc = Jsoup.parse(response.bodyAsText())
        val error = doc.select("div[class=alert alert-danger]").first()?.text()
        when (error) {
            "Время Вашей сессии истекло. Войдите в систему еще раз." -> throw Exception("Session expired")
            "Неверный логин или пароль, попробуйте заново." -> throw Exception("Invalid login or password")
        }
    }

    private fun createHttpClient(): HttpClient {
        return HttpClient(CIO) {
            install(HttpCookies) {
                storage = AcceptAllCookiesStorage()
            }
            install(UserAgent) {
                agent =
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36"
            }
            install(HttpRedirect) {
                checkHttpMethod = true
                allowHttpsDowngrade = false
            }
        }
    }
}
