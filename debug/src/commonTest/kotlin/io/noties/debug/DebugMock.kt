package io.noties.debug

fun Debug.shared.mock(): DebugOutputMock {
    val mock = DebugOutputMock()
    configure { output(mock) }
    return mock
}

fun mockCall(
    formatter: DebugLogMessageFormatter = DefaultDebugLogMessageFormatter,
    log: DebugLog
) = DebugOutputMock.Call(
    formatter = formatter,
    log = log
)

fun mockLog(
    timestamp: String? = null,
    tag: String? = null,
    level: DebugLogLevel? = null,
    vararg message: Any?
): DebugLog = DebugLog(
    timestamp = timestamp,
    tag = tag,
    level = level,
    message = message.asList()
)

class DebugOutputMock : DebugOutput {
    data class Call(
        val formatter: DebugLogMessageFormatter,
        val log: DebugLog
    )

    val calls = mutableListOf<Call>()

    override fun invoke(p1: DebugLogMessageFormatter, p2: DebugLog) {
        calls.add(Call(p1, p2))
    }
}

class DebugLogInterceptorMock : DebugLogInterceptor {

    val logs = mutableListOf<DebugLog>()

    override fun invoke(p1: DebugLog): DebugLog? {
        logs.add(p1.copy())
        return p1
    }
}