package io.noties.debug

data class DebugLog(
    val timestamp: String?,
    val tag: String?,
    val level: DebugLogLevel?,
    val message: List<Any?>
)