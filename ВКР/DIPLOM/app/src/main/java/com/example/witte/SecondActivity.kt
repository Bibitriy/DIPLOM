package com.example.witte

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.witte.currentDiscipline.Discipline
import com.example.witte.currentDiscipline.DisciplineAdapter
import com.example.witte.currentDiscipline.DisciplineWorker
import com.example.witte.databinding.ActivitySecondBinding
import com.example.witte.debtDiscipline.DebtDisciplineWorker
import com.example.witte.scheduleDiscipline.ScheduleAdapter
import com.example.witte.scheduleDiscipline.ScheduleDiscipline
import com.example.witte.scheduleDiscipline.ScheduleDisciplineWorker
import com.example.witte.session.SessionManager
import com.example.witte.userGrades.GradesAdapter
import com.example.witte.userGrades.UserGradesModel
import com.example.witte.userGrades.UserGradesWorker
import com.example.witte.userName.UserNameWorker
import com.google.android.material.navigation.NavigationView
import io.ktor.http.Url
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class SecondActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivitySecondBinding
    private val sessionManager by lazy {
        SessionManager(this, "username", "password")
    }
    private val userNameWorker by lazy {
        UserNameWorker(this)
    }
    private val userGradesWorker by lazy {
        UserGradesWorker(this)
    }
    private val scheduleDisciplineWorker by lazy {
        ScheduleDisciplineWorker(this)
    }
    private val disciplineWorker by lazy {
        DisciplineWorker(this)
    }
    private val debtDisciplineWorker by lazy {
        DebtDisciplineWorker(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigationDrawer()

        runBlocking {
            sessionManager.loadSessionFromFile()
        }

        binding.scheduleRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.scheduleRecyclerView.adapter = ScheduleAdapter(emptyList())

        binding.currentDisciplinesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.currentDisciplinesRecyclerView.adapter = DisciplineAdapter(emptyList())

        binding.debtDisciplinesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.debtDisciplinesRecyclerView.adapter = DisciplineAdapter(emptyList())


        myProfile()

    }

    private fun setupNavigationDrawer() {
        binding.navigationView.setNavigationItemSelectedListener(this)
        val drawerToggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            R.string.drawer_open,
            R.string.drawer_close
        )
        binding.drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        showProperText()

        binding.drawerLayout.openDrawer(GravityCompat.START)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            binding.drawerLayout.openDrawer(GravityCompat.START)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_schedule -> {
                fetchAndDisplaySchedule()
            }

            R.id.nav_current_lessons -> {
                fetchAndDisplayCurrentDisciplines()
            }

            R.id.nav_debts -> {
                fetchAndDisplayDebtDisciplines()
            }

            R.id.nav_grades -> {
                getGrades()
            }

            R.id.nav_student_report -> {
                getStudentReport()
            }

            R.id.nav_support -> {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://online.muiv.ru/hdesk/")
                }
                startActivity(intent)
            }

            R.id.nav_mail -> {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://mail.google.com/a/online.muiv.ru")
                }
                startActivity(intent)
            }

            R.id.nav_logout -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

            R.id.my_profile->{
                myProfile()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun myProfile(){
        binding.webView.visibility =View.GONE
        binding.textMessage.visibility = View.GONE
        binding.scheduleRecyclerView.visibility = View.GONE
        binding.currentDisciplinesRecyclerView.visibility = View.GONE
        binding.debtDisciplinesRecyclerView.visibility = View.GONE
        binding.progressCircular.visibility = View.VISIBLE

        val webView: WebView = binding.webView
        webView.settings.javaScriptEnabled = true
        webView.settings.loadsImagesAutomatically = true
        webView.settings.domStorageEnabled = true
        webView.settings.useWideViewPort = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        webView.webViewClient = object : WebViewClient() {

            override fun onReceivedSslError(view: WebView?, handler: android.webkit.SslErrorHandler?, error: android.net.http.SslError?) {
                handler?.proceed() // Ignore SSL certificate errors for now (not recommended for production)
            }
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                view?.loadUrl("javascript:(function() { " +
                        "var targetDiv1 = document.getElementsByClassName('pffio')[0];" +
                        "var targetDiv2 = document.getElementsByClassName('pffoto')[0];" +
                        "var targetDiv3 = document.getElementsByClassName('pfstudinfo')[0];" +
                        "document.body.innerHTML = '';" +
                        "if (targetDiv1) {" +
                        "document.body.appendChild(targetDiv1);" +
                        "}" +
                        "if (targetDiv2) {" +
                        "document.body.appendChild(targetDiv2);" +
                        "}" +
                        "if (targetDiv3) {" +
                        "document.body.appendChild(targetDiv3);" +
                        "}" +
                        "})()")

                view?.postDelayed({
                    binding.progressCircular.visibility = View.GONE
                    webView.visibility = View.VISIBLE
                }, 1000)
            }
        }
        // Enable cookies
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)

        // Load URL
        val url = "https://e.muiv.ru/local/portfolio/view.php"
        cookieManager.setAcceptCookie(true)
        cookieManager.setCookie(
            url,
            "${sessionManager.cookies.get(0).name}=${sessionManager.cookies.get(0).value}"
        )

        webView.loadUrl(url)
    }

    private fun getStudentReport() {
        val intent = Intent(this@SecondActivity, ThirdActivity::class.java)
        startActivity(intent)
    }

    private fun showProperText() {

        binding.textMessage.visibility = View.GONE
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val scheduleHtml = sessionManager.getPageContent(Url("https://e.muiv.ru/my/"))
                val userName = userNameWorker.parseName(scheduleHtml)


                //scheduleDisciplineWorker.saveScheduleToFile(scheduleDisciplines, "schedule_disciplines.txt")

                runOnUiThread {
                    supportActionBar?.title = "МУИВ ($userName)"
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@SecondActivity, "Ошибка загрузки имени", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    private fun getGrades() {
        binding.webView.visibility =View.GONE
        binding.textMessage.visibility = View.GONE
        binding.scheduleRecyclerView.visibility = View.GONE
        binding.currentDisciplinesRecyclerView.visibility = View.GONE
        binding.debtDisciplinesRecyclerView.visibility = View.GONE
        binding.progressCircular.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val scheduleHtml =
                    sessionManager.getPageContent(Url("https://e.muiv.ru/grade/report/overview/index.php"))
                val userName = userGradesWorker.parseGrades(scheduleHtml)


                //scheduleDisciplineWorker.saveScheduleToFile(scheduleDisciplines, "schedule_disciplines.txt")
                runOnUiThread {
                    binding.scheduleRecyclerView.visibility = View.VISIBLE
                    binding.currentDisciplinesRecyclerView.visibility = View.GONE
                    binding.debtDisciplinesRecyclerView.visibility = View.GONE

                    displayGrades(userName)
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@SecondActivity, e.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun fetchAndDisplaySchedule() {

        binding.webView.visibility =View.GONE
        binding.textMessage.visibility = View.GONE
        binding.scheduleRecyclerView.visibility = View.GONE
        binding.currentDisciplinesRecyclerView.visibility = View.GONE
        binding.debtDisciplinesRecyclerView.visibility = View.GONE
        binding.progressCircular.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val scheduleHtml =
                    sessionManager.getPageContent(Url("https://e.muiv.ru/local/student_timetable/view.php"))
                val scheduleDisciplines = scheduleDisciplineWorker.parseSchedule(scheduleHtml)
                scheduleDisciplineWorker.saveScheduleToFile(
                    scheduleDisciplines,
                    "schedule_disciplines.txt"
                )

                runOnUiThread {
                    binding.scheduleRecyclerView.visibility = View.VISIBLE
                    binding.currentDisciplinesRecyclerView.visibility = View.GONE
                    binding.debtDisciplinesRecyclerView.visibility = View.GONE

                    binding.progressCircular.visibility = View.GONE
                    displaySchedule(scheduleDisciplines)
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this@SecondActivity,
                        "Ошибка загрузки расписания: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }


    private fun fetchAndDisplayCurrentDisciplines() {

        binding.webView.visibility =View.GONE
        binding.textMessage.visibility = View.GONE
        binding.scheduleRecyclerView.visibility = View.GONE
        binding.currentDisciplinesRecyclerView.visibility = View.GONE
        binding.debtDisciplinesRecyclerView.visibility = View.GONE
        binding.progressCircular.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val disciplinesHtml = sessionManager.getPageContent(Url("https://e.muiv.ru/my/"))
                val disciplines = disciplineWorker.parseCurrentDisciplines(disciplinesHtml)
                disciplineWorker.saveDisciplinesToFile(disciplines, "current_disciplines.txt")

                runOnUiThread {
                    binding.currentDisciplinesRecyclerView.visibility = View.VISIBLE
                    binding.scheduleRecyclerView.visibility = View.GONE
                    binding.debtDisciplinesRecyclerView.visibility = View.GONE
                    binding.progressCircular.visibility = View.GONE
                    displayCurrentDisciplines(disciplines)
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this@SecondActivity,
                        "Ошибка загрузки текущих дисциплин: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun fetchAndDisplayDebtDisciplines() {

        binding.webView.visibility =View.GONE
        binding.textMessage.visibility = View.GONE
        binding.progressCircular.visibility = View.VISIBLE
        binding.scheduleRecyclerView.visibility = View.GONE
        binding.currentDisciplinesRecyclerView.visibility = View.GONE
        binding.debtDisciplinesRecyclerView.visibility = View.GONE
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val disciplinesHtml = sessionManager.getPageContent(Url("https://e.muiv.ru/my/"))
                val disciplines = debtDisciplineWorker.parseDisciplines(disciplinesHtml)
                debtDisciplineWorker.saveDisciplinesToFile(disciplines, "debt_disciplines.txt")

                runOnUiThread {
                    binding.scheduleRecyclerView.visibility = View.GONE
                    binding.currentDisciplinesRecyclerView.visibility = View.GONE
                    binding.debtDisciplinesRecyclerView.visibility = View.VISIBLE
                    binding.progressCircular.visibility = View.GONE
                    displayDebtDisciplines(disciplines)
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this@SecondActivity,
                        "Ошибка загрузки текущих дисциплин: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun displaySchedule(scheduleDisciplines: List<ScheduleDiscipline>) {
        val adapter = ScheduleAdapter(scheduleDisciplines)
        binding.scheduleRecyclerView.adapter = adapter
        if (scheduleDisciplines.isEmpty()) {
            binding.textMessage.text = "Извините, в настоящее время расписание отсутствует"
            binding.textMessage.visibility = View.VISIBLE
        }
    }

    private fun displayGrades(grades: List<UserGradesModel>) {
        val adapter = GradesAdapter(grades)
        binding.scheduleRecyclerView.adapter = adapter
        binding.progressCircular.visibility = View.GONE

    }

    private fun displayCurrentDisciplines(disciplines: List<Discipline>) {
        val adapter = DisciplineAdapter(disciplines)
        binding.currentDisciplinesRecyclerView.adapter = adapter

    }

    private fun displayDebtDisciplines(disciplines: List<Discipline>) {
        val adapter = DisciplineAdapter(disciplines)
        binding.debtDisciplinesRecyclerView.adapter = adapter

        if (disciplines.isEmpty()) {
            binding.textMessage.text = "У вас нет задолжностей"
            binding.textMessage.visibility = View.VISIBLE
        }
    }
}
