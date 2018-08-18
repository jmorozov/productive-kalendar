package ru.jmorozov.prodkalendar.service.command.impl

import org.amshove.kluent.mock
import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import ru.jmorozov.prodkalendar.service.command.FileService
import ru.jmorozov.prodkalendar.service.command.SiteParsingService
import java.time.LocalDate
import java.time.Month

object ParsingServiceImplSpec: Spek ({
    given("Parsing service") {
        val mockFileService = mock(FileService::class)
        val mockSiteParsingService = mock(SiteParsingService::class)

        on("parse csv from data.gov.ru") {
            val service = ParsingServiceImpl(
                    "mockUrl",
                    "files/csv/test/test-1.csv",
                    "files/json/test/test-1.json",
                    mockSiteParsingService,
                    mockFileService
            )

            val holidays = service.parseGov()

            it("parse correct") {
                holidays shouldEqual listOf(
                    LocalDate.of(1999, Month.JANUARY, 1),
                    LocalDate.of(1999, Month.JANUARY, 2),
                    LocalDate.of(1999, Month.JANUARY, 10),
                    LocalDate.of(1999, Month.FEBRUARY, 2),
                    LocalDate.of(1999, Month.MARCH, 3),
                    LocalDate.of(1999, Month.APRIL, 4),
                    LocalDate.of(1999, Month.MAY, 30),
                    LocalDate.of(1999, Month.JUNE, 12),
                    LocalDate.of(1999, Month.JULY, 3),
                    LocalDate.of(1999, Month.AUGUST, 11),
                    LocalDate.of(1999, Month.SEPTEMBER, 4),
                    LocalDate.of(1999, Month.OCTOBER, 2),
                    LocalDate.of(1999, Month.NOVEMBER, 28),
                    LocalDate.of(1999, Month.DECEMBER, 26)
                )
            }
        }
    }
})