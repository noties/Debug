package io.noties.debug.outputs

import io.noties.debug.DebugLog
import io.noties.debug.DebugLogMessageFormatter
import io.noties.debug.DebugOutput

class MergeDebugOutput(
    private val outputs: List<DebugOutput>
) : DebugOutput {
    override fun invoke(p1: DebugLogMessageFormatter, p2: DebugLog) {
        for (output in outputs) {
            output(p1, p2)
        }
    }
}