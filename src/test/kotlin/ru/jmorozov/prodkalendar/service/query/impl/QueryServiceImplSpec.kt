package ru.jmorozov.prodkalendar.service.query.impl

import org.amshove.kluent.*
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import ru.jmorozov.prodkalendar.service.command.FileService
import java.time.LocalDate
import java.util.*

object QueryServiceImplSpec: Spek({
    val twoDaysAgo = LocalDate.now().minusDays(2)
    val yesterday = LocalDate.now().minusDays(1)
    val now = LocalDate.now()
    val tomorrow = LocalDate.now().plusDays(1)
    val dayAfterTomorrow = LocalDate.now().plusDays(2)

    given("Query Service and one holiday - $now") {
        val stubFileService = mock(FileService::class)
        When calling stubFileService.readDatesFromJsonFile(any()) itReturns TreeSet(setOf(now))
        val queryService = QueryServiceImpl("someJsonPath", stubFileService)

        val testList = listOf(
            Test(yesterday, tomorrow, 1, 1),
            Test(now, tomorrow, 1, 0),
            Test(yesterday, now, 0, 1),
            Test(now, now, 0, 0),
            Test(twoDaysAgo, yesterday, 0, 1),
            Test(tomorrow, dayAfterTomorrow, 0, 1)
        )

        testList.forEach {
            test ->
            on("count holidays between ${test.from} and ${test.to}") {
                val holidays = queryService.holidaysCountBetween(test.from, test.to)
                it("should return ${test.holidaysCount}") {
                    holidays shouldEqual test.holidaysCount
                }
            }
        }

        testList.forEach {
            test ->
            on("count workdays between ${test.from} and ${test.to}") {
                val holidays = queryService.workdaysCountBetween(test.from, test.to)
                it("should return ${test.workDaysCount}") {
                    holidays shouldEqual test.workDaysCount
                }
            }
        }

        on("holiday") {
            val isHoliday = queryService.isHoliday(now)
            it("should return true") {
                isHoliday.shouldBeTrue()
            }
        }

        on("workday") {
            val isHoliday = queryService.isHoliday(yesterday)
            it("should return false") {
                isHoliday.shouldBeFalse()
            }
        }
    }
})

data class Test(val from: LocalDate, val to: LocalDate, val holidaysCount: Int, val workDaysCount: Int)