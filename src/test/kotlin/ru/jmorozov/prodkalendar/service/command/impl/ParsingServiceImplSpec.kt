package ru.jmorozov.prodkalendar.service.command.impl

import org.amshove.kluent.mock
import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import ru.jmorozov.prodkalendar.service.command.CommandFileService
import ru.jmorozov.prodkalendar.service.command.SiteParsingService
import java.time.LocalDate
import java.time.Month
import java.util.*

object ParsingServiceImplSpec: Spek ({
    given("Parsing service") {
        val mockFileService = mock(CommandFileService::class)
        val mockSiteParsingService = mock(SiteParsingService::class)

        on("parse csv from data.gov.ru") {
            val service = ParsingServiceImpl(
                    "mockUrl",
                    "src/test/resources/files/csv/test.csv",
                    "src/test/resources/files/json/test.json",
                    mockSiteParsingService,
                    mockFileService
            )

            val productiveCalendar = service.parseGov()

            it("holidays parse correctly") {
                productiveCalendar.holidays shouldEqual TreeSet(listOf(
                    LocalDate.of(1999, Month.JANUARY, 1),
                    LocalDate.of(1999, Month.JANUARY, 2),
                    LocalDate.of(1999, Month.JANUARY, 10),
                    LocalDate.of(1999, Month.FEBRUARY, 2),
                    LocalDate.of(1999, Month.FEBRUARY, 4),
                    LocalDate.of(1999, Month.FEBRUARY, 10),
                    LocalDate.of(1999, Month.MARCH, 3),
                    LocalDate.of(1999, Month.MARCH, 5),
                    LocalDate.of(1999, Month.APRIL, 4),
                    LocalDate.of(1999, Month.MAY, 30),
                    LocalDate.of(1999, Month.JUNE, 12),
                    LocalDate.of(1999, Month.JULY, 3),
                    LocalDate.of(1999, Month.JULY, 27),
                    LocalDate.of(1999, Month.AUGUST, 11),
                    LocalDate.of(1999, Month.AUGUST, 28),
                    LocalDate.of(1999, Month.SEPTEMBER, 4),
                    LocalDate.of(1999, Month.SEPTEMBER, 10),
                    LocalDate.of(1999, Month.OCTOBER, 2),
                    LocalDate.of(1999, Month.OCTOBER, 10),
                    LocalDate.of(1999, Month.NOVEMBER, 1),
                    LocalDate.of(1999, Month.NOVEMBER, 2),
                    LocalDate.of(1999, Month.NOVEMBER, 28),
                    LocalDate.of(1999, Month.DECEMBER, 26)
                ))
            }

            it("preholidays parse correctly") {
                productiveCalendar.preholidays shouldEqual TreeSet(listOf(
                        LocalDate.of(1999, Month.JANUARY, 3),
                        LocalDate.of(1999, Month.JANUARY, 11),
                        LocalDate.of(1999, Month.JANUARY, 22),
                        LocalDate.of(1999, Month.APRIL, 30),
                        LocalDate.of(1999, Month.MAY, 1),
                        LocalDate.of(1999, Month.JUNE, 11),
                        LocalDate.of(1999, Month.NOVEMBER, 3),
                        LocalDate.of(1999, Month.DECEMBER, 27),
                        LocalDate.of(1999, Month.DECEMBER, 31)
                ))
            }
        }
    }
})