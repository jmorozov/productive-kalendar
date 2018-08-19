package ru.jmorozov.prodkalendar.service.query.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.jmorozov.prodkalendar.service.command.FileService
import ru.jmorozov.prodkalendar.service.query.QueryService
import java.time.LocalDate
import java.time.Period
import java.util.*

@Service
class QueryServiceImpl @Autowired constructor(
        @Value("\${json.path}") val jsonPath: String,
        val fileService: FileService
): QueryService {
    override fun holidaysCountBetween(from: LocalDate, to: LocalDate): Int {
        val holidays: TreeSet<LocalDate> = fileService.readDatesFromJsonFile(jsonPath)

        return holidays.count { (it.isAfter(from) || it.isEqual(from)) && it.isBefore(to) }
    }

    override fun workdaysCountBetween(from: LocalDate, to: LocalDate): Int {
        val all = Period.between(from, to).days
        val holidays = holidaysCountBetween(from, to)

        return all - holidays
    }

    override fun isHoliday(date: LocalDate): Boolean {
        val holidays: TreeSet<LocalDate> = fileService.readDatesFromJsonFile(jsonPath)

        return holidays.contains(date)
    }
}