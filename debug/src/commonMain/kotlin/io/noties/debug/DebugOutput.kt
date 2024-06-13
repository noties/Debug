package io.noties.debug

typealias DebugOutput = (DebugLogMessageFormatter, DebugLog) -> Unit

val DebugOutputNoOp: DebugOutput = { _, _ -> }