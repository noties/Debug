package io.noties.debug

import io.noties.debug.outputs.AndroidLogDebugOutput
import io.noties.debug.outputs.JavaSystemDebugOutput
import io.noties.debug.outputs.KotlinPrintlnDebugOutput
import io.noties.debug.outputs.MergeDebugOutput
import io.noties.debug.outputs.iOSPrintDebugOutput

/**
 * ```kotlin
 * Debug.configure {
 *
 *   // adds output to android Log (if run on Android)
 *   android
 *
 *   // adds another output to android Log, but with additional minimum log-level setting
 *   android.minimumLevel(DebugLogLevel.INFO)
 * }
 * ```
 */
abstract class DebugConfiguration {
    abstract fun output(output: DebugOutput): DebugConfigurationOutput

    /**
     * Android via android.util.Log.*
     */
    val android: DebugConfigurationOutput get() = output(AndroidLogDebugOutput)

    /**
     * iOS print statements, the same as [kotlin], but available on iOS only
     */
    val iOS: DebugConfigurationOutput get() = output(iOSPrintDebugOutput)

    /**
     * Kotlin println statements (available on all supported platforms)
     */
    val kotlin: DebugConfigurationOutput get() = output(KotlinPrintlnDebugOutput)

    /**
     * Java System.out or System.err outputs (available on JVM and android)
     */
    val java: DebugConfigurationOutput get() = output(JavaSystemDebugOutput)

    fun merge(outputs: List<DebugOutput>) = output(MergeDebugOutput(outputs))
    fun merge(vararg outputs: DebugOutput) = merge(outputs.toList())
}

abstract class DebugConfigurationOutput {
    /**
     * @see DebugConfigurationInterceptor
     */
    abstract fun interceptor(
        interceptor: DebugConfigurationInterceptor.() -> Unit
    ): DebugConfigurationOutput

    // how big is the issue with original timestamp? because executor might queue
    //  pending messages, so tracked timestamp might not reflect teh time log was triggered
    abstract fun executor(executor: DebugLogExecutor): DebugConfigurationOutput

    abstract fun messageFormatter(formatter: DebugLogMessageFormatter): DebugConfigurationOutput

    abstract fun disabled(): DebugConfigurationOutput
}

abstract class DebugConfigurationInterceptor {
    /**
     * Returns self for chaining.
     * First added interceptor would be the first to process the log. If there is some
     * filtering involved, consider placing such interceptors at top for better
     * performance
     */
    abstract fun add(interceptor: DebugLogInterceptor): DebugConfigurationInterceptor

    abstract val size: Int

    fun timestamp(provider: () -> String?) = add { it.copy(timestamp = provider()) }

    fun timestamp(iso8601Pattern: String) = add(TimestampISO8601DebugLogInterceptor(iso8601Pattern))

    /**
     * Sets _minimum_ level for the log entries. Only logs with level equal or greater to the specified one
     * would be emitted. If more granular control is required, then [filter] could be used.
     * For example, `level(DebugLogLevel.INFO)`:
     * * `VERBOSE` -> skipped
     * * `DEBUG` -> skipped
     * * `INFO` -> emitted
     * * `WARN` -> emitted
     * * `ERROR` -> emitted
     */
    fun minimumLevel(level: DebugLogLevel) = add {
        it.takeIf { it.level != null && it.level >= level }
    }

    fun filter(filter: (DebugLog) -> Boolean) = add { it.takeIf(filter) }

    // what if, we pass the number of current interceptors? so, this way we
    //  might be able to obtain proper stack trace on iOS (given we count properly rest of the calls)
    // But then, there is also executor that might change everything
    /**
     * Android only. Intended for debug builds only (non-proguard enabled), as internally
     * inspects thread's current stack trace
     */
    @PrependCallStack
    fun prependCallStack() = add(PrependCallStackDebugLogInterceptor)
}

@RequiresOptIn
@Retention(AnnotationRetention.BINARY)
annotation class PrependCallStack

internal class DebugConfigurationOutputImpl(val output: DebugOutput) : DebugConfigurationOutput() {

    internal val interceptors = mutableListOf<DebugLogInterceptor>()
    internal var executor: DebugLogExecutor? = null
    internal var messageFormatter: DebugLogMessageFormatter? = null
    internal var disabled: Boolean = false

    override fun interceptor(interceptor: DebugConfigurationInterceptor.() -> Unit) = this.also {
        // only one set of interceptors, they are not accumulated between calls
        interceptors.clear()

        val impl = DebugConfigurationInterceptorImpl()
        interceptor(impl)
        interceptors.addAll(impl.interceptors)
    }

    // how big is the issue with original timestamp? because executor might queue
    //  pending messages, so tracked timestamp might not reflect teh time log was triggered
    override fun executor(executor: DebugLogExecutor) = this.also {
        this.executor = executor
    }

    override fun messageFormatter(formatter: DebugLogMessageFormatter) = this.also {
        this.messageFormatter = formatter
    }

    override fun disabled() = this.also {
        this.disabled = true
    }
}

internal class DebugConfigurationInterceptorImpl : DebugConfigurationInterceptor() {

    val interceptors = mutableListOf<DebugLogInterceptor>()

    override fun add(
        interceptor: DebugLogInterceptor
    ): DebugConfigurationInterceptor = this.also {
        if (interceptor != DebugLogInterceptorNoOp) {
            interceptors.add(interceptor)
        }
    }

    override val size: Int
        get() = interceptors.size
}

internal class DebugConfigurationImpl : DebugConfiguration() {
    val outputs = mutableListOf<DebugConfigurationOutputImpl>()

    override fun output(output: DebugOutput): DebugConfigurationOutput {
        return DebugConfigurationOutputImpl(output).also { outputs.add(it) }
    }

    fun createLogger(): DebugLogger? {
        // log entry created once

        val outputs = this.outputs
            .filter { it.output != DebugOutputNoOp }
            .filter { !it.disabled }
            .map { DebugLoggerImpl(it) }
            .takeIf { it.isNotEmpty() }

        val logger: DebugLogger? = outputs?.let { list ->
            { tag, level, message ->
                for (output in list) {
                    output.invoke(tag, level, message)
                }
            }
        }

        return logger
    }
}

internal class DebugLoggerImpl(
    private val executor: DebugLogExecutor?,
    private val interceptors: List<DebugLogInterceptor>,
    private val messageFormatter: DebugLogMessageFormatter,
    private val output: DebugOutput,
) : DebugLogger {

    constructor(output: DebugConfigurationOutputImpl) : this(
        executor = output.executor,
        interceptors = output.interceptors.toList(),
        messageFormatter = output.messageFormatter ?: DefaultDebugLogMessageFormatter,
        output = output.output
    )

    override fun invoke(tag: String?, level: DebugLogLevel, message: Array<out Any?>) {
        if (executor != null) {
            executor.invoke { log(tag, level, message) }
        } else {
            log(tag, level, message)
        }
    }

    private fun log(tag: String?, level: DebugLogLevel, message: Array<out Any?>) {
        // created for each output
        val log = DebugLog(null, tag, level, message.toList())
            .let {
                var log: DebugLog = it
                for (interceptor in interceptors) {
                    log = interceptor(log) ?: return@let null
                }
                log
            }
        if (log != null) {
            output(messageFormatter, log)
        }
    }
}