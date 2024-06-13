package io.noties.debug

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class JavaTimeDebugLogInterceptor(
    iso8601Pattern: String
) : DebugLogInterceptor {

    private val formatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern(iso8601Pattern, Locale.ROOT)

    override fun invoke(p1: DebugLog): DebugLog? {
        // LocalDateTime.now() might be a better option, but as we allow specifying pattern
        //  clients might include there timezone, which is missing from the LocalDateTime
        return p1.copy(
            timestamp = formatter.format(Instant.now().atZone(ZoneId.systemDefault()))
        )
    }
}