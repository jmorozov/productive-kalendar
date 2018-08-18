package ru.jmorozov.prodkalendar.api

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import ru.jmorozov.prodkalendar.service.command.ParsingService

@RestController
class CommandController(val parsingService: ParsingService) {

    @PostMapping("/api/command/parse/gov")
    fun parseGov() = parsingService.parseGov()
}