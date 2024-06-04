package com.example.witte

import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.witte.databinding.ActivityThirdBinding
import com.example.witte.session.SessionManager
import com.example.witte.userReport.AndroidDownloader
import kotlinx.coroutines.runBlocking

class ThirdActivity : AppCompatActivity() {

    private val sessionManager by lazy {
        SessionManager(this, "username", "password")
    }
    lateinit var binding: ActivityThirdBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThirdBinding.inflate(layoutInflater)
        setContentView(binding.root)
        runBlocking {
            sessionManager.loadSessionFromFile()
        }
        supportActionBar?.title = "Мои отчёты"
        val webView: WebView = binding.webView
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                Log.d("check_url", "shouldOverrideUrlLoading: ${request?.url.toString()}")
                val url = request?.url.toString()
                if(url.contains("https://e.muiv.ru/local/student_reports/getreport.php?id=")){
                    webView.loadUrl(url)
                    getStudentReport(url)
                }

                binding.progressBar.visibility = View.VISIBLE
                webView.visibility = View.GONE
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                view?.loadUrl(
                    "javascript:(function() { " + "var targetDiv = document.getElementById('student_report_container');" + "if (targetDiv) {" + "document.body.innerHTML = '';" + "document.body.appendChild(targetDiv);" + "}" + "})()"
                )

                view?.postDelayed({
                    binding.progressBar.visibility = View.GONE
                    webView.visibility = View.VISIBLE
                }, 1000)
            }
        }
        // Enable cookies
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)

        // Load URL
        val url = "https://e.muiv.ru/local/student_reports/view.php?report=ap"
        cookieManager.setAcceptCookie(true)
        cookieManager.setCookie(
            url,
            "${sessionManager.cookies.get(0).name}=${sessionManager.cookies.get(0).value}"
        )

        webView.loadUrl(url)

        binding.back.setOnClickListener(View.OnClickListener {
            this@ThirdActivity.finish()
        })

    }
    private fun getStudentReport(url: String){
        val downloader = AndroidDownloader(context = this)
        downloader.downloadFile(url, sessionManager.cookies)
        Toast.makeText(baseContext, "Загрузка началась", Toast.LENGTH_SHORT).show()
        this@ThirdActivity.finish()

    }

}