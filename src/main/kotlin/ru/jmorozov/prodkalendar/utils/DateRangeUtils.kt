package ru.jmorozov.prodkalendar.utils

import java.time.LocalDate
import javax.validation.ValidationException
import ru.jmorozov.prodkalendar.dto.DateRange

fun normalizeDateRange(range: DateRange) {
    if (range.start == null && range.end == null) {
        throw ValidationException("Start and end together must not be null")
    }

    if (range.start == null) {
        range.start = LocalDate.now()
    }
    if (range.end == null) {
        range.end = LocalDate.now()
    }
    if (range.start == range.end) {
        if (range.start!!.isBefore(LocalDate.now())) {
            range.end = LocalDate.now()
        } else {
            range.start = LocalDate.now()
        }
    } else if (range.end!!.isBefore(range.start)) {
        val tmp = range.end
        range.end = range.start
        range.start = tmp
    }
}