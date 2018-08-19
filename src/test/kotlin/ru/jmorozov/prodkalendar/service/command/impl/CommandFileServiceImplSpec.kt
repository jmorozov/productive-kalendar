package ru.jmorozov.prodkalendar.service.command.impl

import com.fasterxml.jackson.module.kotlin.readValue
import org.amshove.kluent.shouldBeGreaterThan
import org.amshove.kluent.shouldContainAll
import org.amshove.kluent.shouldExist
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.api.dsl.xon
import ru.jmorozov.prodkalendar.config.JacksonConfig
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDate

object CommandFileServiceImplSpec: Spek ({
    val objectMapper = JacksonConfig().objectMapper()
    val fileService = CommandFileServiceImpl(objectMapper)

    given("Command file service") {
        val path = "src/test/resources/files/create/addresses.csv"
        val pathToFile = Paths.get(path)

        afterEachTest {
            Files.deleteIfExists(pathToFile)
            Files.deleteIfExists(pathToFile.parent)
        }

        on("create file") {
            val file = fileService.createFile(path)
            it("create directories and create file") {
                file.shouldExist()
            }
        }

        // Чтобы не тратить время каждый раз на проверку загрузки, отключим префиксом x
        xon("download") {
            fileService.download("https://people.sc.fsu.edu/~jburkardt/data/csv/addresses.csv", path)
            val file = pathToFile.toFile()
            it("should download file") {
                file.shouldExist()
                file.length() shouldBeGreaterThan 0
            }
        }
    }

    given("Command json file service") {
        val path = "src/test/resources/files/write/test.json"
        val pathToJsonFile = Paths.get(path)
        val dateList = listOf(LocalDate.now())

        afterEachTest {
            Files.deleteIfExists(pathToJsonFile)
            Files.deleteIfExists(pathToJsonFile.parent)
        }

        on("write dates to json file") {
            fileService.writeDatesToJsonFile(dateList, path)

            val holidays: List<LocalDate> = objectMapper.readValue(pathToJsonFile.toFile())
            it("should write list of dates to json") {
                holidays shouldContainAll dateList
            }
        }
    }
})