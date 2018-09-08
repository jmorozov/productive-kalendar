package ru.jmorozov.prodkalendar.dto

import java.time.LocalDate
import java.util.*

data class ProductiveCalendar(
    val holidays: TreeSet<LocalDate> = TreeSet(),
    val preholidays: TreeSet<LocalDate> = TreeSet()
)
