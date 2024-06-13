package io.noties.debug.sample.jvm

import io.noties.debug.DebugLog
import io.noties.debug.DebugLogMessageFormatter
import io.noties.debug.DebugOutput
import java.io.File

class FileDebugOutput : DebugOutput {
    override fun invoke(formatter: DebugLogMessageFormatter, log: DebugLog) {
        // of cause it should be properly created, for example, for each date a new log file,
        //  so they could also be cleaned up
        val file = File("build/logs.txt")
        val text = formatter(log)
        if (text != null) {
            file.appendText("$text\n")
        }
    }
}