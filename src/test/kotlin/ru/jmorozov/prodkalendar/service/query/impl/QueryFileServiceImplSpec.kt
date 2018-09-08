package ru.jmorozov.prodkalendar.service.query.impl

import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import ru.jmorozov.prodkalendar.config.JacksonConfig
import ru.jmorozov.prodkalendar.dto.ProductiveCalendar
import java.time.LocalDate
import java.time.Month
import java.util.*

object QueryFileServiceImplSpec: Spek({
    given("Query file service") {
        val queryFileService = QueryFileServiceImpl(JacksonConfig().objectMapper())
        val holidays = TreeSet(setOf(LocalDate.of(1999, Month.JANUARY, 2)))
        val preholidays = TreeSet(setOf(LocalDate.of(1999, Month.JANUARY, 1)))
        val productiveCalendar = ProductiveCalendar(holidays, preholidays)

        on("read productive calendar from json file") {
            val productiveCalendarFromJson = queryFileService.readProductiveCalendarFromJsonFile("src/test/resources/files/json/test.json")
            it("should return dates tree set") {
                productiveCalendarFromJson shouldEqual productiveCalendar
            }
        }
    }
})