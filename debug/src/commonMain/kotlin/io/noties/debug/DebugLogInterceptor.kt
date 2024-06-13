package io.noties.debug

typealias DebugLogInterceptor = (DebugLog) -> DebugLog?

val DebugLogInterceptorNoOp: DebugLogInterceptor = { it }