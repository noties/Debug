package io.noties.debug

actual val PrependCallStackDebugLogInterceptor: DebugLogInterceptor = {
    val callStack = obtainCallStack()
    if (callStack == null) {
        it
    } else {
        it.copy(
            message = listOf(callStack) + it.message
        )
    }
}

private val DEBUG_NAME = Debug::class.java.name
private val DEBUG_IMPL_NAME = (DEBUG_NAME + "Impl").also {
    // validate it exists, must be caught during testing
    Class.forName(it)
}

private fun obtainCallStack(): String? {
    // NB! At first it was looking just for first different package from the library,
    //  but would mal-function if a custom DebugOutput was configured, so it would be
    //  mistakenly considered caller, meanwhile it is not. Instead we search for `Debug`
    //  presence in the stack trace and use the next step after it as the caller. Multiple
    //  debug entries are possible (as different methods might be called)
    val stackTrace = Throwable().stackTrace

    var foundDebugImpl = false

    // okay... what if there is proguard.. well, this should not be used in production
    //  or proguard-enabled projects
    for (entry in stackTrace) {

        // `entry.className.startsWith(DEBUG_NAME)`, hm, actually no,
        //      as all internal classes would indicate it as true, and then, any customer
        //      provided class would be considered a call stack (for example, interceptor)

        val className = entry.className

        val isDebugImpl = className == DEBUG_IMPL_NAME
        if (isDebugImpl) {
            foundDebugImpl = true
        } else {
            // if we saw debug, then this entry is what we are looking for
            if (foundDebugImpl && !className.startsWith(DEBUG_NAME)) {
                return "${entry.methodName}(${entry.fileName}:${entry.lineNumber})"
            }
        }
    }

    return null
}