package ru.jmorozov.prodkalendar

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ProdKalendarApplication

fun main(args: Array<String>) {
    runApplication<ProdKalendarApplication>(*args)
}
