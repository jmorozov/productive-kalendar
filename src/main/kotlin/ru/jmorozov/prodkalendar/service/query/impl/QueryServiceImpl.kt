package ru.jmorozov.prodkalendar.service.query.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.jmorozov.prodkalendar.dto.DayType
import ru.jmorozov.prodkalendar.dto.ProductiveCalendar
import ru.jmorozov.prodkalendar.service.query.QueryFileService
import ru.jmorozov.prodkalendar.service.query.QueryService
import java.time.LocalDate
import java.time.Month
import java.time.Period
import java.time.Year
import java.util.*

@Service
class QueryServiceImpl @Autowired constructor(
    @Value("\${json.path}") private val jsonPath: String,
    private val fileService: QueryFileService
) : QueryService {

    override fun holidaysCountBetween(from: LocalDate, to: LocalDate): Int =
            getAllHolidays().count { inDateRange(it, from, to) }

    override fun getAllHolidays(): TreeSet<LocalDate> = getProductiveCalendar().holidays

    override fun getProductiveCalendar(): ProductiveCalendar = fileService.readProductiveCalendarFromJsonFile(jsonPath)

    private fun inDateRange(it: LocalDate, from: LocalDate, to: LocalDate): Boolean =
            (it.isAfter(from) || it.isEqual(from)) && it.isBefore(to)

    override fun workdaysCountBetween(from: LocalDate, to: LocalDate): Int {
        val all = Period.between(from, to).days
        val holidays = holidaysCountBetween(from, to)

        return all - holidays
    }

    override fun isHoliday(date: LocalDate): Boolean = getAllHolidays().contains(date)

    override fun isTomorrowHoliday(): Boolean = isHoliday(getTomorrow())

    private fun getTomorrow(): LocalDate = LocalDate.now().plusDays(1)

    override fun getHolidaysByYear(year: Year): TreeSet<LocalDate> = getDaysByYear(year, ::getAllHolidays)

    private fun getDaysByYear(year: Year, getDays: () -> TreeSet<LocalDate>): TreeSet<LocalDate> {
        val from = LocalDate.of(year.value, Month.JANUARY, 1)
        val to = from.plusYears(1)

        return TreeSet(getDays().filter { inDateRange(it, from, to) })
    }

    override fun getProductiveCalendarByYear(year: Year): ProductiveCalendar =
            ProductiveCalendar(getHolidaysByYear(year), getPreholidaysByYear(year))

    private fun getPreholidaysByYear(year: Year) = getDaysByYear(year, ::getAllPreholidays)

    private fun getAllPreholidays(): TreeSet<LocalDate> = getProductiveCalendar().preholidays

    override fun getDayType(date: LocalDate): DayType = when {
        isHoliday(date) -> DayType.HOLIDAY
        isPreholiday(date) -> DayType.PREHOLIDAY
        else -> DayType.WORKDAY
    }

    private fun isPreholiday(date: LocalDate): Boolean = getAllPreholidays().contains(date)

    override fun getTomorrowDayType(): DayType = getDayType(getTomorrow())
}