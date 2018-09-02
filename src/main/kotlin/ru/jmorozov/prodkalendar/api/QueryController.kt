package ru.jmorozov.prodkalendar.api

import org.springframework.web.bind.annotation.*
import ru.jmorozov.prodkalendar.dto.DateRange
import ru.jmorozov.prodkalendar.service.query.QueryService
import ru.jmorozov.prodkalendar.utils.normalizeDateRange
import java.time.LocalDate
import java.time.Year
import java.time.format.DateTimeParseException
import java.util.*
import javax.validation.ValidationException

@RestController
class QueryController(private val queryService: QueryService) {

    private companion object {
        const val MIN_YEAR = 1999
        const val MAX_YEAR = 2100
    }

    @PostMapping("/api/query/holidays/between")
    fun getHolidaysBetween(@RequestBody range: DateRange): Int {
        normalizeDateRange(range)

        return queryService.holidaysCountBetween(range.start!!, range.end!!)
    }

    @PostMapping("/api/query/workdays/between")
    fun getWorkdaysBetween(@RequestBody range: DateRange): Int {
        normalizeDateRange(range)

        return queryService.workdaysCountBetween(range.start!!, range.end!!)
    }

    @GetMapping("/api/query/is/{dateStr}/holiday")
    fun isHoliday(@PathVariable("dateStr") dateStr: String): Boolean {
        try {
            val date: LocalDate = LocalDate.parse(dateStr)

            return queryService.isHoliday(date)
        } catch (e: DateTimeParseException) {
            throw ValidationException("Incorrect date in request", e)
        }
    }

    @GetMapping("/api/query/is/tomorrow/holiday")
    fun isTomorrowHoliday(): Boolean = queryService.isTomorrowHoliday()

    @GetMapping("/api/query/all/holidays")
    fun getAllHolidays(): TreeSet<LocalDate> = queryService.getAllHolidays()

    @GetMapping("/api/query/{year}/holidays")
    fun getHolidaysByYear(@PathVariable("year") year: Int): TreeSet<LocalDate> {
        if (year < MIN_YEAR || year > MAX_YEAR) {
            return TreeSet()
        }

        return queryService.getHolidaysByYear(Year.of(year))
    }
}