package ru.jmorozov.prodkalendar.service.command

import java.io.File
import java.time.LocalDate
import java.util.*

interface FileService {
    fun download(href: String, pathToSave: String)
    fun createFile(path: String): File
    fun writeDatesToJsonFile(holidays: List<LocalDate>, pathToSave: String)
    fun readDatesFromJsonFile(pathToFile: String): TreeSet<LocalDate>
}