package ru.jmorozov.prodkalendar.service.command.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.jmorozov.prodkalendar.service.command.FileService
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.nio.channels.Channels
import java.nio.file.Files
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import javax.cache.annotation.CacheResult

@Service
class FileServiceImpl: FileService {

    companion object {
        val log: Logger = LoggerFactory.getLogger(FileServiceImpl::class.java.name)
    }

    override fun download(href: String, pathToSave: String) {
        val fileChannel = FileOutputStream(createFile(pathToSave)).channel

        val url = URL(href)
        val readableByteChannel = Channels.newChannel(url.openStream())
        fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE)

        log.info("CSV file saved from $url to $pathToSave")
    }

    override fun createFile(path: String): File {
        val pathToFile = Paths.get(path)
        Files.createDirectories(pathToFile.parent)
        Files.deleteIfExists(pathToFile)
        return pathToFile.toFile()
    }

    override fun writeDatesToJsonFile(holidays: List<LocalDate>, pathToSave: String) {
        getObjectMapper().writerWithDefaultPrettyPrinter()
                .with(SimpleDateFormat("dd.MM.yyyy"))
                .writeValue(createFile(pathToSave), holidays)
    }

    private fun getObjectMapper(): ObjectMapper =  jacksonObjectMapper()
            .registerModule(JavaTimeModule())
            .registerModule(KotlinModule())
            .configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)

    @CacheResult(cacheName = "holidays")
    override fun readDatesFromJsonFile(pathToFile: String): TreeSet<LocalDate> {
        log.info("Read dates from $pathToFile")
        return getObjectMapper().readValue(File(pathToFile))
    }
}