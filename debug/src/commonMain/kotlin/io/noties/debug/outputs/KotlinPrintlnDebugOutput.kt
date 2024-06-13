package io.noties.debug.outputs

import io.noties.debug.DebugOutput

val KotlinPrintlnDebugOutput: DebugOutput = { formatter, log ->
    val message = formatter(log)
    println(message)
}