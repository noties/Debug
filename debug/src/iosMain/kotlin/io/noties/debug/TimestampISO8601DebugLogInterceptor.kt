package io.noties.debug

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter

@Suppress("FunctionName")
actual fun TimestampISO8601DebugLogInterceptor(iso8601Pattern: String): DebugLogInterceptor {
    return iOSTimeStampDebugLogInterceptor(iso8601Pattern)
}

@Suppress("ClassName")
class iOSTimeStampDebugLogInterceptor(iso8601Pattern: String) : DebugLogInterceptor {

    private val formatter = NSDateFormatter().also {
        it.dateFormat = iso8601Pattern
    }

    override fun invoke(p1: DebugLog): DebugLog? {
        return p1.copy(
            timestamp = formatter.stringFromDate(NSDate())
        )
    }
}