package ru.jmorozov.prodkalendar.service.query.impl

import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import ru.jmorozov.prodkalendar.config.JacksonConfig
import java.time.LocalDate
import java.time.Month
import java.util.*

object QueryFileServiceImplSpec: Spek({
    given("Query file service") {
        val queryFileService = QueryFileServiceImpl(JacksonConfig().objectMapper())

        on("read dates from json file") {
            val holidays = queryFileService.readDatesFromJsonFile("src/test/resources/files/json/test.json")
            it("should return dates tree set") {
                holidays shouldEqual TreeSet(setOf(LocalDate.of(1999, Month.JANUARY, 1)))
            }
        }
    }
})