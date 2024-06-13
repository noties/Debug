package io.noties.debug.outputs

import io.noties.debug.DebugOutput

// see for possible integration: https://github.com/gumob/CallStackParser

// Whoa, https://stackoverflow.com/a/54844203
//  just suppressing it resolves compilation error
@Suppress("NO_ACTUAL_FOR_EXPECT", "ClassName")
expect val iOSPrintDebugOutput: DebugOutput