package ru.jmorozov.prodkalendar.service.command.impl

import org.amshove.kluent.shouldStartWith
import org.amshove.kluent.shouldThrow
import org.amshove.kluent.withMessage
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.api.dsl.xgiven

object SiteParsingServiceImplSpec: Spek({
    // Отключим, чтобы не тратить время в общем случае (приставка x == @Ignore)
    xgiven("Site parsing service") {
        val siteParsingService = SiteParsingServiceImpl()
        val url = "https://data.gov.ru/opendata/7708660670-proizvcalendar"

        on("correct css query selector") {
            val href = siteParsingService.getDownloadHref(url, "a[href*=UTF-8]:contains(Последний набор)")

            it("should return href") {
                href shouldStartWith "https://"
            }
        }

        on("incorrect css query selector") {
            val func = { siteParsingService.getDownloadHref(url, "a[href*=UTF-8]:contains(Последний герой)") }

            it("should throw exception") {
                func shouldThrow RuntimeException::class withMessage "href for download not found in $url"
            }
        }
    }
})