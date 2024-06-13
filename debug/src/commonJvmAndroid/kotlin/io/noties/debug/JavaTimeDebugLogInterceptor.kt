package io.noties.debug

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

// as it is a shared class between both android and jvm, we cannot just put @TargetApi
//  annotation... (or can we add explicit annotation dependency and specify it?)
class JavaTimeDebugLogInterceptor(
    iso8601Pattern: String
) : DebugLogInterceptor {

    private val formatter: DateTimeFormatter =
        //noinspection NewApi
        DateTimeFormatter.ofPattern(iso8601Pattern, Locale.ROOT)

    override fun invoke(p1: DebugLog): DebugLog? {
        // LocalDateTime.now() might be a better option, but as we allow specifying pattern
        //  clients might include there timezone, which is missing from the LocalDateTime
        return p1.copy(
            //noinspection NewApi
            timestamp = formatter.format(Instant.now().atZone(ZoneId.systemDefault()))
        )
    }
}