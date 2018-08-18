package ru.jmorozov.prodkalendar.service.command.impl

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVRecord
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.jmorozov.prodkalendar.service.command.FileService
import ru.jmorozov.prodkalendar.service.command.ParsingService
import ru.jmorozov.prodkalendar.service.command.SiteParsingService
import java.io.FileReader
import java.time.LocalDate
import javax.cache.annotation.CacheRemoveAll


@Service
class ParsingServiceImpl @Autowired constructor(
        @Value("\${gov.url}") val govUrl: String,
        @Value("\${csv.path}") val csvPath: String,
        @Value("\${json.path}") val jsonPath: String,
        val siteParsingService: SiteParsingService,
        val fileService: FileService
) : ParsingService {

    enum class Months(val number: Int,val rus: String) {
        JANUARY(1, "Январь"),
        FEBRUARY(2, "Февраль"),
        MARCH(3, "Март"),
        APRIL(4, "Апрель"),
        MAY(5, "Май"),
        JUNE(6, "Июнь"),
        JULY(7, "Июль"),
        AUGUST(8, "Август"),
        SEPTEMBER(9, "Сентябрь"),
        OCTOBER(10, "Октябрь"),
        NOVEMBER(11, "Ноябрь"),
        DECEMBER(12, "Декабрь")
    }

    companion object {
        val log = LoggerFactory.getLogger(ParsingServiceImpl::class.java.name)
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
            for (month in Months.values()) {
                val holidaysStr: String = record.get(month.rus) ?: throw RuntimeException("Holidays not found in month $month")
                // Звездочкой (*) отмечены предпраздничные (сокращенные) дни. Плюсом (+) отмечены перенесенные выходные дни.
                val days: List<String> = holidaysStr
                        .replace(Regex("""\+|,\d\*|,\d\d\*|\d\*,|\d\d\*,"""), "") // удаляем лишние символы и предпраздничные дни
                        .split(Regex(""",|\+,|,\d\*"""), 31)
                for (day in days) {
                    holidays.add(LocalDate.of(year.toInt(), month.number, day.toInt()))
                }
            }
        }

        return holidays
    }

    private fun getRecords(): Iterable<CSVRecord>  = CSVFormat.DEFAULT.withHeader(
                "Год/Месяц", "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь",
                "Ноябрь", "Декабрь", "Всего рабочих дней", "Всего праздничных и выходных дней",
                "Количество рабочих часов при 40-часовой рабочей неделе", "Количество рабочих часов при 36-часовой рабочей неделе",
                "Количество рабочих часов при 24-часовой рабочей неделе"
            ).withFirstRecordAsHeader()
            .parse(FileReader(csvPath))

}