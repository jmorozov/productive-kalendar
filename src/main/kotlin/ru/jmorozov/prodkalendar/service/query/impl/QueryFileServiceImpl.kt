package ru.jmorozov.prodkalendar.service.query.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.jmorozov.prodkalendar.service.query.QueryFileService
import java.io.File
import java.time.LocalDate
import java.util.*
import javax.cache.annotation.CacheResult

@Service
class QueryFileServiceImpl @Autowired constructor(
        private val objectMapper: ObjectMapper
) : QueryFileService {

    private companion object {
        val log: Logger = LoggerFactory.getLogger(QueryFileServiceImpl::class.java.name)
    }

    @CacheResult(cacheName = "holidays")
    override fun readDatesFromJsonFile(pathToFile: String): TreeSet<LocalDate> {
        log.info("Read dates from $pathToFile")

        return objectMapper.readValue(File(pathToFile))
    }
}