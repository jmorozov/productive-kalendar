package ru.jmorozov.prodkalendar.service.query.impl

import org.amshove.kluent.*
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import ru.jmorozov.prodkalendar.dto.DayType
import ru.jmorozov.prodkalendar.dto.ProductiveCalendar
import ru.jmorozov.prodkalendar.service.query.QueryFileService
import java.time.LocalDate
import java.time.Year
import java.util.*

object QueryServiceImplSpec: Spek({
    val twoDaysAgo = LocalDate.now().minusDays(2)
    val yesterday = LocalDate.now().minusDays(1)
    val now = LocalDate.now()
    val tomorrow = LocalDate.now().plusDays(1)
    val dayAfterTomorrow = LocalDate.now().plusDays(2)
    val nextYearNow = now.plusYears(1)
    val allHolidays = TreeSet(setOf(now, nextYearNow))
    val year = Year.now()
    val nextYear = year.plusYears(1)
    val nextYearYesterday = yesterday.plusYears(1)
    val allPreholidays = TreeSet(setOf(yesterday, nextYearYesterday))
    val productiveCalendar = ProductiveCalendar(allHolidays, allPreholidays)
    val nextYearHolidays = TreeSet(setOf(nextYearNow))
    val nextYearPreholidays = TreeSet(setOf(nextYearYesterday))
    val nextYearProductiveCalendar = ProductiveCalendar(nextYearHolidays, nextYearPreholidays)

    given("Query Service and one holiday - $now") {
        val stubFileService = mock(QueryFileService::class)
        When calling stubFileService.readProductiveCalendarFromJsonFile(any()) itReturns productiveCalendar
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

        on("is tomorrow holiday") {
            val isHoliday = queryService.isTomorrowHoliday()
            it("should return false") {
                isHoliday.shouldBeFalse()
            }
        }

        on("get all holidays") {
            val holidays = queryService.getAllHolidays()
            it("should return all holidays") {
                holidays shouldEqual allHolidays
            }
        }

        on("get holidays by $nextYear year") {
            val holidays = queryService.getHolidaysByYear(nextYear)
            it("should return all holidays in $nextYear year") {
                holidays shouldEqual nextYearProductiveCalendar.holidays
            }
        }

        on("get productive calendar") {
            val result = queryService.getProductiveCalendar()
            it("should return productive calendar") {
                result shouldEqual productiveCalendar
            }
        }

        on("get productive calendar by $nextYear year") {
            val result = queryService.getProductiveCalendarByYear(nextYear)
            it("should return productive calendar for $nextYear year") {
                result shouldEqual nextYearProductiveCalendar
            }
        }

        mapOf(
                yesterday to DayType.PREHOLIDAY,
                now to DayType.HOLIDAY,
                tomorrow to DayType.WORKDAY
        ).forEach {
            day, expected ->
            on("get day type for $expected day") {
                val dayType = queryService.getDayType(day)
                it("should return $expected type") {
                    dayType shouldEqual expected
                }
            }
        }

        on("get tomorrow day type") {
            val dayType = queryService.getTomorrowDayType()
            it("should return tomorrow day type") {
                dayType shouldEqual DayType.WORKDAY
            }
        }
    }
})

data class Test(val from: LocalDate, val to: LocalDate, val holidaysCount: Int, val workDaysCount: Int)