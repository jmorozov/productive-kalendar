package ru.jmorozov.prodkalendar.api

import org.springframework.web.bind.annotation.*
import ru.jmorozov.prodkalendar.dto.DateRange
import ru.jmorozov.prodkalendar.service.query.QueryService
import ru.jmorozov.prodkalendar.utils.normalizeDateRange
import java.time.LocalDate

@RestController
class QueryController(val queryService: QueryService) {

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

    @GetMapping("/api/query/{dateStr}/is/holiday")
    fun isHoliday(@PathVariable("dateStr") dateStr: String): Boolean {
        val date: LocalDate = LocalDate.parse(dateStr)

        return queryService.isHoliday(date)
    }
}