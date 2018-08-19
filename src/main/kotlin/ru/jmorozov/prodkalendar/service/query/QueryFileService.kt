package ru.jmorozov.prodkalendar.service.query

import java.time.LocalDate
import java.util.*

interface QueryFileService {
    fun readDatesFromJsonFile(pathToFile: String): TreeSet<LocalDate>
}