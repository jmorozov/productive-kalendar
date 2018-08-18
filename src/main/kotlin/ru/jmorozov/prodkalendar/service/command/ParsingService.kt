package ru.jmorozov.prodkalendar.service.command

import java.time.LocalDate

interface ParsingService {
    fun parseGov(): List<LocalDate>
}