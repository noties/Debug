@file:Suppress("ClassName")

package io.noties.debug.outputs

import io.noties.debug.DebugOutput

actual val iOSPrintDebugOutput : DebugOutput = { formatter, log ->
    val text = formatter(log)
    println(text)
}