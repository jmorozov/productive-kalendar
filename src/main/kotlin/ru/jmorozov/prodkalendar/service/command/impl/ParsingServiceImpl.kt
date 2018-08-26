package ru.jmorozov.prodkalendar.service.command.impl

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.jmorozov.prodkalendar.exception.CsvParsingException
import ru.jmorozov.prodkalendar.service.command.CommandFileService
import ru.jmorozov.prodkalendar.service.command.ParsingService
import ru.jmorozov.prodkalendar.service.command.SiteParsingService
import java.io.FileReader
import java.time.LocalDate
import java.time.Month
import javax.cache.annotation.CacheRemoveAll

@Service
class ParsingServiceImpl @Autowired constructor(
    @Value("\${gov.url}") private val govUrl: String,
    @Value("\${csv.path}") private val csvPath: String,
    @Value("\${json.path}") private val jsonPath: String,
    private val siteParsingService: SiteParsingService,
    private val fileService: CommandFileService
) : ParsingService {

    enum class MonthsL11n(val number: Int, val rus: String) {
        JANUARY(Month.JANUARY.value, "Январь"),
        FEBRUARY(Month.FEBRUARY.value, "Февраль"),
        MARCH(Month.MARCH.value, "Март"),
        APRIL(Month.APRIL.value, "Апрель"),
        MAY(Month.MAY.value, "Май"),
        JUNE(Month.JUNE.value, "Июнь"),
        JULY(Month.JULY.value, "Июль"),
        AUGUST(Month.AUGUST.value, "Август"),
        SEPTEMBER(Month.SEPTEMBER.value, "Сентябрь"),
        OCTOBER(Month.OCTOBER.value, "Октябрь"),
        NOVEMBER(Month.NOVEMBER.value, "Ноябрь"),
        DECEMBER(Month.DECEMBER.value, "Декабрь")
    }

    private companion object {
        val log: Logger = LoggerFactory.getLogger(ParsingServiceImpl::class.java.name)
        const val MAX_DAYS_COUNT_IN_MONTH = 31
    }

    @CacheRemoveAll(cacheName = "holidays")
    override fun parseGov(): List<LocalDate> {
        val downloadHref = siteParsingService.getDownloadHref(govUrl, "a[href*=UTF-8]:contains(Последний набор)")
        fileService.download(downloadHref, csvPath)
        val holidays: List<LocalDate> = getHolidaysFromCSV()
        fileService.writeDatesToJsonFile(holidays, jsonPath)

        log.debug("Holidays: $holidays")
        log.info("Holidays wrote to a file $jsonPath")

        return holidays
    }

    private fun getHolidaysFromCSV(): List<LocalDate> {
        val holidays: MutableList<LocalDate> = mutableListOf()
        for (record: CSVRecord in getRecords()) {
            val year = record.get("Год/Месяц")
            for (month in MonthsL11n.values()) {
                val holidaysStr: String = record.get(month.rus) ?: throw CsvParsingException("Holidays not found in month $month")
                // Звездочкой (*) отмечены предпраздничные (сокращенные) дни. Плюсом (+) отмечены перенесенные выходные дни.
                val days: List<String> = holidaysStr
                        .replace(Regex("""\+|,\d\*|,\d\d\*|\d\*,|\d\d\*,"""), "") // удаляем лишние символы и предпраздничные дни
                        .split(Regex(""",|\+,|,\d\*"""), MAX_DAYS_COUNT_IN_MONTH)
                for (day in days) {
                    holidays.add(LocalDate.of(year.toInt(), month.number, day.toInt()))
                }
            }
        }

        return holidays
    }

    private fun getRecords(): Iterable<CSVRecord> = CSVFormat.DEFAULT.withHeader(
                "Год/Месяц", MonthsL11n.JANUARY.rus, MonthsL11n.FEBRUARY.rus, MonthsL11n.MARCH.rus, MonthsL11n.APRIL.rus,
                MonthsL11n.MAY.rus, MonthsL11n.JUNE.rus, MonthsL11n.JULY.rus, MonthsL11n.AUGUST.rus, MonthsL11n.SEPTEMBER.rus,
                MonthsL11n.OCTOBER.rus, MonthsL11n.NOVEMBER.rus, MonthsL11n.DECEMBER.rus, "Всего рабочих дней",
                "Всего праздничных и выходных дней", "Количество рабочих часов при 40-часовой рабочей неделе",
                "Количество рабочих часов при 36-часовой рабочей неделе", "Количество рабочих часов при 24-часовой рабочей неделе"
            ).withFirstRecordAsHeader()
            .parse(FileReader(csvPath))
}