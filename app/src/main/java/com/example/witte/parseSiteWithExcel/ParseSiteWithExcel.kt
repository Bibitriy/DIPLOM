package com.example.witte.parseSiteWithExcel

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements


class ParseSiteWithExcel {
    //TODO get data from SiteConfig
    private val baseUrl: String = "https://www.muiv.ru/"
    private var scheduleUrl = "${baseUrl}studentu/fakultety-i-kafedry/fakultet-it/raspisaniya/"

    suspend fun siteData(): ArrayList<SiteScheduleUnit>? {
        val doc: Document = Jsoup.connect(scheduleUrl).timeout(6000).get()
        val scheduleSection: Elements = doc.select("div.m-intext-docs")
        val result: ArrayList<SiteScheduleUnit> = arrayListOf()
        if (scheduleSection.size == 0) {
            return null
        }

        for (scheduleUnit in scheduleSection) {
            val mDocElements: Elements = scheduleUnit.select("div.m-doc")
            for (mDocElement in mDocElements) {
                var downloadName: String? = null
                var date: String? = null
                var downloadLink: String? = null
                for (element in mDocElement.children()) {
                    when {
                        element.className() == "m-doc__data" && element.text()
                            .startsWith("Дата обновления:") -> {
                            date = element.text().substringAfter("Дата обновления: ")
                        }

                        element.className() == "m-doc__name" -> {
                            val aTag = element.select("a").first()
                            downloadName = aTag?.text()
                            downloadLink = aTag?.attr("href")
                        }
                    }
                }
                if (downloadName != null && date != null && downloadLink != null) {
                    result.add(SiteScheduleUnit(downloadName, date, "${baseUrl}${downloadLink}"))
                }
            }
        }
        if (result.size > 0) {
            return  result
        } else {
            return  null
        }
    }
}