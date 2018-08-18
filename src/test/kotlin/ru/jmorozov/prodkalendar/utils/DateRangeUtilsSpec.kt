package ru.jmorozov.prodkalendar.utils

import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldThrow
import org.amshove.kluent.withMessage
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import ru.jmorozov.prodkalendar.dto.DateRange
import java.time.LocalDate
import javax.validation.ValidationException

object DateRangeUtilsSpec: Spek({
    given("normalize date range") {
        val now = LocalDate.now()
        val afterNow = now.plusMonths(1)
        val beforeNow = now.minusMonths(1)

        on("start and end equals null") {
            val dateRange = DateRange(null, null)
            val func = { normalizeDateRange(dateRange) }
            it("thrown IllegalArgumentException") {
                func shouldThrow ValidationException::class withMessage "Start and end together must not be null"
            }
        }

        listOf(
            Test("without start and end after now", DateRange(null, afterNow), "start should equals now", DateRange(now, afterNow)),
            Test("without end and start before now", DateRange(beforeNow, null), "end should equals now", DateRange(beforeNow, now)),
            Test("start equals end and before now", DateRange(beforeNow, beforeNow), "end should equals now", DateRange(beforeNow, now)),
            Test("start equals end and after now", DateRange(afterNow, afterNow), "start should equals now", DateRange(now, afterNow)),
            Test("end before start", DateRange(afterNow, beforeNow), "should swap range values", DateRange(beforeNow, afterNow)),
            Test("start before end", DateRange(beforeNow, afterNow), "should do nothing", DateRange(beforeNow, afterNow)),
            Test("start and before equals now", DateRange(now, now), "should do nothing", DateRange(now, now))
        ).forEach {
            test ->
            on(test.case) {
                val dateRange = test.input
                normalizeDateRange(dateRange)
                it(test.should) {
                    dateRange shouldEqual test.expected
                }
            }
        }
    }
})

data class Test(val case: String, val input: DateRange, val should: String, val expected: DateRange)