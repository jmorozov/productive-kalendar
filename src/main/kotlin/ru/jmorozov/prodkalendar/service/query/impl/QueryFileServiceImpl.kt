package ru.jmorozov.prodkalendar.service.query.impl

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File
import java.io.IOException
import javax.cache.annotation.CacheResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.jmorozov.prodkalendar.dto.ProductiveCalendar
import ru.jmorozov.prodkalendar.service.query.QueryFileService

@Service
class QueryFileServiceImpl @Autowired constructor(
    private val objectMapper: ObjectMapper
) : QueryFileService {

    private companion object {
        val log: Logger = LoggerFactory.getLogger(QueryFileServiceImpl::class.java.name)
    }

    @CacheResult(cacheName = "productiveCalendar")
    override fun readProductiveCalendarFromJsonFile(pathToFile: String): ProductiveCalendar {
        log.info("Read productive calendar from $pathToFile")

        try {
            return objectMapper.readValue(File(pathToFile))
        } catch (e: Exception) {
            when (e) {
                is JsonParseException, is JsonMappingException ->
                    log.error("Can not parse productive calendar from json")
                is IOException ->
                    log.error("Can not read productive calendar json from $pathToFile")
            }
            throw e
        }
    }
}