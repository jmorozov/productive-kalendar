package ru.jmorozov.prodkalendar.service.command.impl

import org.jsoup.Jsoup
import org.springframework.stereotype.Service
import ru.jmorozov.prodkalendar.service.command.SiteParsingService

@Service
class SiteParsingServiceImpl: SiteParsingService {

    override fun getDownloadHref(url: String, cssQuery: String): String {
        val doc = Jsoup.connect(url).get()

        return doc.select(cssQuery)
                .first()
                ?.attr("href")
                ?: throw RuntimeException("href for download not found in $url")
    }
}