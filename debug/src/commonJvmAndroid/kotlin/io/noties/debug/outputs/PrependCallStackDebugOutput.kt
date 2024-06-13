//package io.noties.debug.outputs
//
//import io.noties.debug.Debug
//import io.noties.debug.DebugLogLevel
//import io.noties.debug.DebugOutput
//
//class PrependCallStackDebugOutput constructor(
//    private val output: DebugOutput
//)  {
//    private companion object {
//        private val DEBUG_NAME = Debug::class.java.name
//    }
//
//    override fun output(
//        tag: String?,
//        level: DebugLogLevel,
//        message: List<Any?>
//    ) {
//        output.output(tag, level, message)
//    }
//
//    override fun processMessage(message: List<Any?>): String? {
//        return super.processMessage(listOf(obtainCallStack()) + message)
//    }
//
//    private fun obtainCallStack(): String? {
//        // NB! At first it was looking just for first different package from the library,
//        //  but would mal-function if a custom DebugOutput was configured, so it would be
//        //  mistakenly considered caller, meanwhile it is not. Instead we search for `Debug`
//        //  presence in the stack trace and use the next step after it as the caller. Multiple
//        //  debug entries are possible (as different methods might be called)
//        val stackTrace = Throwable().stackTrace
//
//        var foundDebug = false
//
//        // okay... what if there is proguard.. well, this should not be used in production
//        //  or proguard-enabled projects
//        for (entry in stackTrace) {
//            val isDebug = DEBUG_NAME == entry.className
//            if (isDebug) {
//                foundDebug = true
//            } else {
//                // if we saw debug, then this entry is what we are looking for
//                if (foundDebug) {
//                    return "${entry.methodName}(${entry.className}:${entry.lineNumber})"
//                }
//            }
//        }
//
//        return null
//    }
//}