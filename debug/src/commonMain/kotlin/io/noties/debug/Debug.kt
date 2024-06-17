package io.noties.debug

import kotlin.jvm.JvmStatic

interface Debug {
    // current tag, null if root
    val tag: String?

    // creates a new tag
    fun tag(tag: String): Debug

    // Pass exception as one of the arguments, it would be processed and stack-trace printed
    fun v(vararg message: Any?)

    fun d(vararg message: Any?)

    fun i(vararg message: Any?)

    fun w(vararg message: Any?)

    fun e(vararg message: Any?)

    fun log(level: DebugLogLevel, vararg message: Any?)

    @Suppress("ClassName")
    companion object shared: Debug by impl {
        @JvmStatic
        fun configure(configuration: DebugConfiguration.() -> Unit): Debug {
            val impl = DebugConfigurationImpl()
            configuration(impl)
            logger = impl.createLogger()
            return this
        }
    }

    // no import unambiguity, just a single version, so no additional questions
    // can be used as a short-hand to pass to some printing functions
//        val func = Debug::i
//
//        fun accpt(accepts: (Array<Any?>) -> Unit) {
//
//        }
//        accpt(Debug::i)
}