package ru.jmorozov.prodkalendar.service.query

import ru.jmorozov.prodkalendar.dto.ProductiveCalendar

interface QueryFileService {
    fun readProductiveCalendarFromJsonFile(pathToFile: String): ProductiveCalendar
}