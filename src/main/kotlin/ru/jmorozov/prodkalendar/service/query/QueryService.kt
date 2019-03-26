package ru.jmorozov.prodkalendar.service.query

import java.time.LocalDate
import java.time.Year
import java.util.*
import ru.jmorozov.prodkalendar.dto.DayType
import ru.jmorozov.prodkalendar.dto.ProductiveCalendar

interface QueryService {
    fun holidaysCountBetween(from: LocalDate, to: LocalDate): Int
    fun workdaysCountBetween(from: LocalDate, to: LocalDate): Int

    fun isHoliday(date: LocalDate): Boolean
    fun isTomorrowHoliday(): Boolean

    fun getAllHolidays(): TreeSet<LocalDate>
    fun getHolidaysByYear(year: Year): TreeSet<LocalDate>

    fun getProductiveCalendar(): ProductiveCalendar
    fun getProductiveCalendarByYear(year: Year): ProductiveCalendar

    fun getDayType(date: LocalDate): DayType
    fun getTomorrowDayType(): DayType
}