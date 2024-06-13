package io.noties.debug

import java.io.PrintWriter
import java.io.StringWriter

actual val DefaultDebugLogMessageFormatter: DebugLogMessageFormatter = {

    fun processException(exception: Throwable): String {
        val stringWriter = StringWriter()
        val printWriter = PrintWriter(stringWriter)
        exception.printStackTrace(printWriter)
        return stringWriter.toString()
    }

    val body = it.message.fold("") { out, value ->
        if (value is Throwable) {
            "$out\n${processException(value)}"
        } else {
            if (out.isEmpty()) {
                "$value"
            } else {
                "$out $value"
            }
        }
    }

    // each of them has a fallback, so regex can work (space would be still inserted)
    listOfNotNull(
        it.timestamp,
        it.tag?.let { "#$it" },
        it.level?.let { "[${it.name.first()}]" },
        body
    ).joinToString(" ")
}