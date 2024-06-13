package io.noties.debug

actual val DefaultDebugLogMessageFormatter: DebugLogMessageFormatter = {
    listOfNotNull(
        it.timestamp,
        it.level?.let { "[${it.name.first()}]" },
        it.tag?.let { "#$it" },
        it.message.joinToString(" ")
    ).joinToString(" ")
}