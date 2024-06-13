package io.noties.debug

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class DebugConfigurationTest {

    @Test
    fun `output - added`() {
        val output = Debug.mock()

        val message = arrayOf(1, 2, 3, null)

        Debug.v(*message)
        Debug.d(*message)
        Debug.i(*message)
        Debug.w(*message)
        Debug.e(*message)

        val calls = DebugLogLevel.entries
            .map { mockCall(log = mockLog(level = it, message = message)) }

        // first call will create them
        assertEquals(
            calls,
            output.calls
        )
    }

    @Test
    fun `output - ignored`() {
        val output = DebugOutputNoOp
        val configuration = DebugConfigurationImpl()
        configuration.output(output)

        assertNull(configuration.createLogger())
    }

    @Test
    fun `output - filtered`() {
        val present = DebugOutputMock()
        val noOp = DebugOutputNoOp

        val configuration = DebugConfigurationImpl()
        configuration.output(present)
        configuration.output(noOp)

        val logger = configuration.createLogger()
        assertNotNull(logger)

        logger.invoke(null, DebugLogLevel.ERROR, arrayOf("hasta mañana"))

        assertEquals(
            listOf(
                mockCall(
                    log = mockLog(
                        level = DebugLogLevel.ERROR,
                        message = *(arrayOf("hasta mañana"))
                    )
                )
            ),
            present.calls
        )
    }

    @Test
    fun `interceptor - nullStops`() {
        // interceptor that returns nil, stops log entry propagation

        val tag = "hello-tag-123"

        val mock = DebugOutputMock()
        Debug.configure {
            output(mock)
                .interceptor {
                    add { it.takeIf { it.level == DebugLogLevel.ERROR } }
                }
        }

        DebugLogLevel.entries
            .forEach {
                Debug.log(it)
                Debug.tag(tag).log(it)
            }

        assertEquals(
            listOf(
                mockCall(log = mockLog(level = DebugLogLevel.ERROR)),
                mockCall(log = mockLog(level = DebugLogLevel.ERROR, tag = tag))
            ),
            mock.calls
        )
    }

    @Test
    fun `interceptor - minimumLevel`() {
        // so, verbose takes all
        // so, debug does not take verbose, but all others

        val entries = DebugLogLevel.entries

        data class Input(
            val level: DebugLogLevel,
            val accepts: Set<DebugLogLevel>
        )

        // holds currently accepted level during level iteration
        val accepts = entries.toMutableSet()
        assertEquals(
            setOf(
                DebugLogLevel.VERBOSE,
                DebugLogLevel.DEBUG,
                DebugLogLevel.INFO,
                DebugLogLevel.WARN,
                DebugLogLevel.ERROR
            ),
            accepts
        )

        val inputs = mutableListOf<Input>()

        for (level in entries) {
            inputs.add(Input(level, accepts.toSet()))
            accepts.remove(level)
        }

        val message = "hello 3.1415F"

        for ((level, accepts) in inputs) {
            val output = DebugOutputMock().also {
                Debug.configure {
                    output(it)
                        .interceptor {
                            minimumLevel(level)
                        }
                }
            }

            entries.forEach { entry ->
                val shouldOutput = accepts.contains(entry)
                val calls = output.calls.size
                Debug.log(entry, message)
//                println("level:$level entry:$entry should:$shouldOutput")
                if (shouldOutput) {
                    assertEquals(calls + 1, output.calls.size, output.calls.toString())
                    assertEquals(
//                        mockCall(
//                            level = entry,
//                            message = listOf(message)
//                        ),
                        mockCall(log = mockLog(level = entry, message = arrayOf(message))),
                        output.calls.lastOrNull(),
                        output.calls.toString()
                    )
                } else {
                    assertEquals(calls, output.calls.size, output.calls.toString())
                }
            }
        }
    }

    @Test
    fun executor() {
        val actions = mutableListOf<() -> Unit>()
        val output = DebugOutputMock().also {
            Debug.configure {
                output(it)
                    .executor {
                        actions.add(it)
                    }
            }
        }

        (0..<10).forEach { Debug.i(it) }

        assertEquals(emptyList(), output.calls)

        for ((i, action) in actions.withIndex()) {
            // 0 based, before we trigger action, it equals number of previous invocations
            action()
            assertEquals(
                mockCall(log = mockLog(level = DebugLogLevel.INFO, message = *(arrayOf(i)))),
                output.calls.last()
            )
        }
    }
}