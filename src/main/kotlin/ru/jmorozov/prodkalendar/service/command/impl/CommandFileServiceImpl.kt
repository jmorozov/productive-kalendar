package ru.jmorozov.prodkalendar.service.command.impl

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.jmorozov.prodkalendar.dto.ProductiveCalendar
import ru.jmorozov.prodkalendar.service.command.CommandFileService
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.nio.channels.Channels
import java.nio.file.Files
import java.nio.file.Paths
import java.text.SimpleDateFormat

@Service
class CommandFileServiceImpl @Autowired constructor(
    private val objectMapper: ObjectMapper
) : CommandFileService {

    private companion object {
        val log: Logger = LoggerFactory.getLogger(CommandFileServiceImpl::class.java.name)
    }

    override fun download(href: String, pathToSave: String) {
        val fileChannel = FileOutputStream(createFile(pathToSave)).channel

        val url = URL(href)
        Channels.newChannel(url.openStream()).use { readableByteChannel ->
            fileChannel.use { fch ->
                fch.transferFrom(readableByteChannel, 0, Long.MAX_VALUE)
            }
        }

        log.info("File saved from $url to $pathToSave")
    }

    override fun createFile(path: String): File {
        val pathToFile = Paths.get(path)
        Files.createDirectories(pathToFile.parent)
        Files.deleteIfExists(pathToFile)
        Files.createFile(pathToFile)
        return pathToFile.toFile()
    }

    override fun writeDatesToJsonFile(productiveCalendar: ProductiveCalendar, pathToSave: String) {
        objectMapper.writerWithDefaultPrettyPrinter()
                .with(SimpleDateFormat("dd.MM.yyyy"))
                .writeValue(createFile(pathToSave), productiveCalendar)
    }
}