package io.noties.debug

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class JavaCalendarDebugLogInterceptor(
    iso8601Pattern: String
) : DebugLogInterceptor {

    private val formatter = SimpleDateFormat(iso8601Pattern, Locale.ROOT)

    override fun invoke(p1: DebugLog): DebugLog? {
        return p1.copy(
            timestamp = formatter.format(Date())
        )
    }
}