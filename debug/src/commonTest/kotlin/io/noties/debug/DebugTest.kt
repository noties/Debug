package io.noties.debug

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DebugTest {
    @Test
    fun `tag - root`() {
        // root is empty
        assertNull(Debug.tag)
    }

    @Test
    fun `tag - fork`() {
        val kitchen = Debug.tag("kitchen")
        assertEquals("kitchen", kitchen.tag)

        val fork = kitchen.tag("fork")
        assertEquals("kitchen/fork", fork.tag)

        val spoon = kitchen.tag("spoon")
        assertEquals("kitchen/spoon", spoon.tag)
    }

    @Test
    fun `log - v`() {
        assertLog(
            level = DebugLogLevel.VERBOSE,
            message = Debug::v
        )
    }

    @Test
    fun `log - d`() {
        assertLog(
            level = DebugLogLevel.DEBUG,
            message = Debug::d
        )
    }

    @Test
    fun `log - i`() {
        assertLog(
            level = DebugLogLevel.INFO,
            message = Debug::i
        )
    }

    @Test
    fun `log - w`() {
        assertLog(
            level = DebugLogLevel.WARN,
            message = Debug::w,
        )
    }

    @Test
    fun `log - e`() {
        assertLog(
            level = DebugLogLevel.ERROR,
            message = Debug::e
        )
    }

    private fun assertLog(
        level: DebugLogLevel,
        message: (Array<Any?>) -> Unit
    ) {
        val mock = Debug.mock()

        val message1: List<Any?> = listOf("hello", 42)
        message(message1.toTypedArray())
        assertEquals(1, mock.calls.size)

        assertEquals(
            listOf(mockCall(log = mockLog(level = level, message = *(message1.toTypedArray())))),
            mock.calls
        )
    }
}