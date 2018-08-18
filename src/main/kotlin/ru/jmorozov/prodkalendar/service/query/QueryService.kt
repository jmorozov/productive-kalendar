package ru.jmorozov.prodkalendar.service.query

import java.time.LocalDate

interface QueryService {
    fun holidaysCountBetween(from: LocalDate, to: LocalDate): Int
    fun workdaysCountBetween(from: LocalDate, to: LocalDate): Int

    fun isHoliday(date: LocalDate): Boolean
}