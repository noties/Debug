package io.noties.debug

internal val impl: Debug = DebugImpl(null)

internal typealias DebugLogger = (tag: String?, level: DebugLogLevel, message: Array<out Any?>) -> Unit

internal var logger: DebugLogger? = null

internal class DebugImpl(
    override val tag: String?
) : Debug {
    override fun tag(tag: String): Debug {
        val outTag = listOfNotNull(this.tag, tag)
            .joinToString("/")
            .takeIf { it.isNotEmpty() }
        return if (outTag.isNullOrEmpty()) {
            this
        } else {
            DebugImpl(outTag)
        }
    }

    override fun v(vararg message: Any?) {
        logger?.invoke(tag, DebugLogLevel.VERBOSE, message)
    }

    override fun d(vararg message: Any?) {
        logger?.invoke(tag, DebugLogLevel.DEBUG, message)
    }

    override fun i(vararg message: Any?) {
        logger?.invoke(tag, DebugLogLevel.INFO, message)
    }

    override fun w(vararg message: Any?) {
        logger?.invoke(tag, DebugLogLevel.WARN, message)
    }

    override fun e(vararg message: Any?) {
        logger?.invoke(tag, DebugLogLevel.ERROR, message)
    }

    override fun log(level: DebugLogLevel, vararg message: Any?) {
        logger?.invoke(tag, level, message)
    }
}