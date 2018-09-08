package ru.jmorozov.prodkalendar.service.command

import ru.jmorozov.prodkalendar.dto.ProductiveCalendar

interface ParsingService {
    fun parseGov(): ProductiveCalendar
}