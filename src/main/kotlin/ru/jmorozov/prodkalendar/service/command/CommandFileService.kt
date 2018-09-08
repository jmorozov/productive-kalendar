package ru.jmorozov.prodkalendar.service.command

import java.io.File
import ru.jmorozov.prodkalendar.dto.ProductiveCalendar

interface CommandFileService {
    fun download(href: String, pathToSave: String)
    fun createFile(path: String): File
    fun writeDatesToJsonFile(productiveCalendar: ProductiveCalendar, pathToSave: String)
}